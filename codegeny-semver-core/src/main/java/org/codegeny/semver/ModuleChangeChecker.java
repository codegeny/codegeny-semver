package org.codegeny.semver;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;

public class ModuleChangeChecker implements ChangeChecker<Module> {
	
	public static ModuleChangeChecker newConfiguredInstance() {
		java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ModuleChangeChecker.class.getName());
		return newConfiguredInstance((f, a) -> logger.info(() -> String.format(f, a)));
	}
	
	public static ModuleChangeChecker newConfiguredInstance(Logger logger) {
		
		Set<Metadata> metaSet = new HashSet<>();
		ServiceLoader.load(Metadata.class).forEach(metaSet::add);
		Metadata metadata = metaSet.stream().reduce(Metadata::and).orElseGet(Metadata.Default::new);
		
		ModuleChangeChecker moduleChangeChecker = new ModuleChangeChecker(logger);
		
		Consumer<ChangeChecker<?>> initializer = c -> {
			if (c instanceof MetadataAware) {
				((MetadataAware) c).setMetadata(metadata);
			}
			
			if (c instanceof LoggerAware) {
				((LoggerAware) c).setLogger(logger);
			}
		};
		
		ServiceLoader.load(ClassChangeChecker.class).forEach(c -> {
			initializer.accept(c);
			moduleChangeChecker.addClassChangeChecker(c);
		});
		
		ServiceLoader.load(MethodChangeChecker.class).forEach(c -> {
			initializer.accept(c);
			moduleChangeChecker.addMethodChangeChecker(c);
		});
		
		ServiceLoader.load(FieldChangeChecker.class).forEach(c -> {
			initializer.accept(c);
			moduleChangeChecker.addFieldChangeChecker(c);
		});

		ServiceLoader.load(ConstructorChangeChecker.class).forEach(c -> {
			initializer.accept(c);
			moduleChangeChecker.addConstructorChangeChecker(c);
		});
		
		ServiceLoader.load(GenericDeclarationChangeChecker.class).forEach(c -> {
			initializer.accept(c);
			moduleChangeChecker.addClassChangeChecker(c);
			moduleChangeChecker.addMethodChangeChecker(c);
			moduleChangeChecker.addConstructorChangeChecker(c);
		});
		
		ServiceLoader.load(MemberChangeChecker.class).forEach(c -> {
			initializer.accept(c);
			moduleChangeChecker.addMethodChangeChecker(c);
			moduleChangeChecker.addFieldChangeChecker(c);
			moduleChangeChecker.addConstructorChangeChecker(c);
		});
		
		ServiceLoader.load(ExecutableChangeChecker.class).forEach(c -> {
			initializer.accept(c);
			moduleChangeChecker.addMethodChangeChecker(c);
			moduleChangeChecker.addConstructorChangeChecker(c);
		});
		
		return moduleChangeChecker;
	}
	
	private final Set<ChangeChecker<? super Class<?>>> classChangeCheckers = new HashSet<>();
	private final Set<ChangeChecker<? super Constructor<?>>> constructorChangeCheckers = new HashSet<>();
	private final Set<ChangeChecker<? super Field>> fieldChangeCheckers = new HashSet<>();
	private final Logger logger;
	private final Set<ChangeChecker<? super Method>> methodChangeCheckers = new HashSet<>();

	public ModuleChangeChecker(Logger logger) {
		this.logger = logger;
	}

	public void addClassChangeChecker(ChangeChecker<? super Class<?>> classChangeChecker) {
		this.classChangeCheckers.add(classChangeChecker);
	}
	
	public void addConstructorChangeChecker(ChangeChecker<? super Constructor<?>> constructorChangeChecker) {
		this.constructorChangeCheckers.add(constructorChangeChecker);
	}
	
	public void addFieldChangeChecker(ChangeChecker<? super Field> fieldChangeChecker) {
		this.fieldChangeCheckers.add(fieldChangeChecker);
	}
	
	public void addMethodChangeChecker(ChangeChecker<? super Method> methodChangeChecker) {
		this.methodChangeCheckers.add(methodChangeChecker);
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
				
			ClassRealm commonRealm = classWorld.newRealm("common", Thread.currentThread().getContextClassLoader());
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
				
				if (previousClass == currentClass) { // same classloader
					continue;
				}
				
				Change classResult = Change.PATCH;
				int checks = 0;
				
				for (ChangeChecker<? super Class<?>> checker : this.classChangeCheckers) {
					
					checks++;
					
					classResult = classResult.combine(checker.check(previousClass, currentClass));
					
					if (classResult == Change.MAJOR) {
						break;
					}
				}
				
				if (classResult == Change.MAJOR) {
					break;
				}
				
				Map<String, Field> previousFields = Stream.of(previousClass.getDeclaredFields()).collect(toMap(Field::getName, f -> f));
				Map<String, Field> currentFields = Stream.of(currentClass.getDeclaredFields()).collect(toMap(Field::getName, f -> f));
				
				Set<String> fieldNames = new HashSet<>();
				fieldNames.addAll(previousFields.keySet());
				fieldNames.addAll(currentFields.keySet());
				
				for (Object fieldName : fieldNames) {
					
					Field previousField = previousFields.get(fieldName);
					Field currentField = currentFields.get(fieldName);
					
					for (ChangeChecker<? super Field> checker : this.fieldChangeCheckers) {
					
						checks++;
						
						classResult = classResult.combine(checker.check(previousField, currentField));
						
						if (classResult == Change.MAJOR) {
							break;
						}
					}
					
					if (classResult == Change.MAJOR) {
						break;
					}
				}
				
				if (classResult == Change.MAJOR) {
					break;
				}
				
				Map<Object, Method> previousMethods = Stream.of(previousClass.getDeclaredMethods()).collect(toMap(m -> key(m), m -> m));
				Map<Object, Method> currentMethods = Stream.of(currentClass.getDeclaredMethods()).collect(toMap(m -> key(m), m -> m));
				
				Set<Object> methodKeys = new HashSet<>();
				methodKeys.addAll(previousMethods.keySet());
				methodKeys.addAll(currentMethods.keySet());
				
				for (Object methodKey : methodKeys) {
					
					Method previousMethod = previousMethods.get(methodKey);
					Method currentMethod = currentMethods.get(methodKey);
					
					for (ChangeChecker<? super Method> checker : this.methodChangeCheckers) {
					
						checks++;
						
						classResult = classResult.combine(checker.check(previousMethod, currentMethod));
						
						if (classResult == Change.MAJOR) {
							break;
						}
					}
					
					if (classResult == Change.MAJOR) {
						break;
					}
				}
				
				if (classResult == Change.MAJOR) {
					break;
				}
				
				Map<Object, Constructor<?>> previousConstructors = Stream.of(previousClass.getDeclaredConstructors()).collect(toMap(m -> key(m), m -> m));
				Map<Object, Constructor<?>> currentCosntructors = Stream.of(currentClass.getDeclaredConstructors()).collect(toMap(m -> key(m), m -> m));
				
				Set<Object> constructorKeys = new HashSet<>();
				constructorKeys.addAll(previousConstructors.keySet());
				constructorKeys.addAll(currentCosntructors.keySet());
				
				for (Object constructorKey : constructorKeys) {
					
					Constructor<?> previousConstructor = previousConstructors.get(constructorKey);
					Constructor<?> currentConstructor = currentCosntructors.get(constructorKey);
					
					for (ChangeChecker<? super Constructor<?>> checker : this.constructorChangeCheckers) {
					
						checks++;
						
						classResult = classResult.combine(checker.check(previousConstructor, currentConstructor));
						
						if (classResult == Change.MAJOR) {
							break;
						}
					}
					
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
	
	private Object key(Method method) {
		return new HashKey(method.getName(), new HashKey((Object[]) method.getParameterTypes()));
	}
	
	private Object key(Constructor<?> constructor) {
		return new HashKey((Object[]) constructor.getParameterTypes());
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
