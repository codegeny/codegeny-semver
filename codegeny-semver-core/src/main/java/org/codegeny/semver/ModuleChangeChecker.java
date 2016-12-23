package org.codegeny.semver;

import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;

public class ModuleChangeChecker implements ChangeChecker<Module> {
	
	public static ModuleChangeChecker newConfiguredInstance() {
		java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ModuleChangeChecker.class.getName());
		return newConfiguredInstance((f, a) -> logger.info(() -> String.format(f, a)));
	}
	
	public static ModuleChangeChecker newConfiguredInstance(Logger logger) {
		ModuleChangeChecker moduleChangeChecker = new ModuleChangeChecker(logger);
		ServiceLoader.load(ClassChangeChecker.class).forEach(moduleChangeChecker::addClassChangeChecker);
		return moduleChangeChecker;
	}
	
	private final Set<ClassChangeChecker> classChangeCheckers = new HashSet<>();
	private final Logger logger;

	public ModuleChangeChecker(Logger logger) {
		this.logger = logger;
	}

	public void addClassChangeChecker(ClassChangeChecker classChangeChecker) {
		this.classChangeCheckers.add(classChangeChecker);
	}
	
	@Override
	public Change check(Module previous, Module current) {
		
		Set<URL> previousDependencies = toURLs(previous.getDependencies());
		Set<URL> currentDependencies = toURLs(current.getDependencies());
		
		Set<URL> commonDependencies = new HashSet<>(previousDependencies);
		commonDependencies.retainAll(currentDependencies);
		
		previousDependencies.removeAll(commonDependencies);
		currentDependencies.remove(commonDependencies);
		
		ClassWorld classWorld = new ClassWorld();
		
		try (
				
			ClassRealm commonRealm = classWorld.newRealm("common");
			ClassRealm previousRealm = commonRealm.createChildRealm("previous");
			ClassRealm currentRealm = commonRealm.createChildRealm("current")) {
		
			commonDependencies.forEach(commonRealm::addURL);
			
			previousRealm.addURL(toURL(previous.getMain()));
			previousDependencies.forEach(previousRealm::addURL);
			
			currentRealm.addURL(toURL(current.getMain()));
			currentDependencies.forEach(currentRealm::addURL);
			
			Set<String> classNames = new HashSet<>();
			classNames.addAll(previous.getClassNames());
			classNames.addAll(current.getClassNames());
	
			Change globalResult = Change.PATCH;
			
			for (String className : classNames) {
				
				Class<?> previousClass = getClass(className, previousRealm);
				Class<?> currentClass = getClass(className, currentRealm);
				
				if (previousClass == currentClass) {
					continue;
				}
				
				Change classResult = Change.PATCH;
				int checks = 0;
				
				for (ClassChangeChecker checker : this.classChangeCheckers) {
					
					checks++;
					
					classResult = classResult.combine(checker.check(previousClass, currentClass));
					
					if (classResult == Change.MAJOR) {
						break;
					}
				}
		
				logger.log("%s :: %s (%d)", classResult, className, checks);
				
				globalResult = globalResult.combine(classResult);
				
				if (globalResult == Change.MAJOR) {
					break;
				}
			}
			
			return globalResult;
			
		} catch (DuplicateRealmException duplicateRealmException) {
			throw new RuntimeException(duplicateRealmException);
		} catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}
	}
	
	private Class<?> getClass(String className, ClassRealm realm) {
		try {
			return realm.loadClass(className);
		} catch (ClassNotFoundException classNotFoundException) {
			return null;
		}
	}
	
	private URL toURL(File file) {
		try {
			return file.toURI().toURL();
		} catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}
	}
	
	private Set<URL> toURLs(Set<File> files) {
		return files.stream().map(this::toURL).collect(toSet());
	}
}
