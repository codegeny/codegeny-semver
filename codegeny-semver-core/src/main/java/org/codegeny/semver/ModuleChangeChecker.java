package org.codegeny.semver;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ModuleChangeChecker implements ChangeChecker<Module> {
		
	public static ModuleChangeChecker newConfiguredInstance(Logger logger) {
		
		Set<Metadata> metaSet = new HashSet<>();
		ServiceLoader.load(Metadata.class).forEach(metaSet::add);
		Metadata metadata = metaSet.stream().reduce(Metadata::and).orElseGet(Metadata.Default::new);
		
		ModuleChangeChecker moduleChangeChecker = new ModuleChangeChecker(logger, metadata);
		
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
	private final Metadata metadata;
	private final Set<ChangeChecker<? super Method>> methodChangeCheckers = new HashSet<>();

	public ModuleChangeChecker(Logger logger, Metadata metadata) {
		this.logger = logger;
		this.metadata = metadata;
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
		
		Set<URL> previousArchives = toURLs(previous.getArchives());
		Set<URL> currentArchives = toURLs(current.getArchives());
		
		Set<URL> commonArchives = new HashSet<>(previousArchives);
		commonArchives.retainAll(currentArchives);
		
		previousArchives.removeAll(commonArchives);
		currentArchives.remove(commonArchives);
				
		try (
				
			URLClassLoader commonLoader = newClassLoader(commonArchives, Thread.currentThread().getContextClassLoader());
			URLClassLoader previousLoader = newClassLoader(previousArchives, commonLoader);
			URLClassLoader currentLoader = newClassLoader(currentArchives, commonLoader)) {
			
			Set<String> classNames = new HashSet<>();
			classNames.addAll(previous.getClassNames());
			classNames.addAll(current.getClassNames());
	
			Change globalResult = Change.PATCH;
			
			for (String className : classNames) {
				
				Class<?> previousClass = getClass(className, previousLoader);
				Class<?> currentClass = getClass(className, currentLoader);
				
				if (previousClass == null && currentClass == null) {
					throw new RuntimeException("Could not load " + className + " at all!!!");
				}
				
				if (previousClass == currentClass) { // same classloader
					continue;
				}
				
				List<Supplier<Change>> checkers = new LinkedList<>();
				checkers.add(() -> check(previousClass, currentClass, classChangeCheckers, metadata::isPublicAPI));
				checkers.add(() -> check(previousClass.getDeclaredFields(), currentClass.getDeclaredFields(), fieldChangeCheckers, metadata::isPublicAPI, Field::getName));
				checkers.add(() -> check(previousClass.getDeclaredMethods(), currentClass.getDeclaredMethods(), methodChangeCheckers, metadata::isPublicAPI, this::key));
				checkers.add(() -> check(previousClass.getDeclaredConstructors(), currentClass.getDeclaredConstructors(), constructorChangeCheckers, metadata::isPublicAPI, this::key));
				
				Change classResult = combine(checkers);
				
				globalResult = globalResult.combine(classResult);
				
				if (globalResult == Change.MAJOR) {
					break;
				}
			}
			
			return globalResult;
			
		} catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}
	}
	
	private <T> Change check(T previous, T current, Iterable<ChangeChecker<? super T>> checkers, Predicate<T> isPublicAPI) {
		Change result = Change.PATCH;
		if (previous != null && !isPublicAPI.test(previous) && current != null && !isPublicAPI.test(current)) {
			return result;
		}
		logger.log("+ %s", previous != null ? previous : current);
		for (ChangeChecker<? super T> checker : checkers) {
			Change change = checker.check(previous, current);
			logger.log("  - %s : %s", change, checker.getClass().getSimpleName());
			result = result.combine(change);
			if (result == Change.MAJOR) {
				break;
			}
		}
		logger.log("= %s", result);
		return result;
	}
	
	private <T> Change check(T[] previouses, T[] currents, Iterable<ChangeChecker<? super T>> checkers, Predicate<T> isPublicAPI, Function<T, Object> keyer) {
		Map<Object, T> previousMap = Stream.of(previouses).collect(toMap(keyer, m -> m));
		Map<Object, T> currentMap = Stream.of(currents).collect(toMap(keyer, m -> m));
		Set<Object> keys = new HashSet<>();
		keys.addAll(previousMap.keySet());
		keys.addAll(currentMap.keySet());
		Change result = Change.PATCH;
		for (Object key : keys) {
			T previous = previousMap.get(key);
			T current = currentMap.get(key);
			if (previous == current) {
				continue;
			}
			result = result.combine(check(previous, current, checkers, isPublicAPI));
			if (result == Change.MAJOR) {
				break;
			}
		}
		return result;
	}
	
	private Change combine(Iterable<Supplier<Change>> checkers) {
		Change result = Change.PATCH;
		for (Supplier<Change> checker : checkers) {
			result = result.combine(checker.get());
			if (result == Change.MAJOR) {
				break;
			}
		}
		return result;
	}
	
	private Class<?> getClass(String className, ClassLoader classLoader) {
		try {
			return classLoader.loadClass(className);
		} catch (ClassNotFoundException classNotFoundException) {
			return null;
		}
	}
	
	private Object key(Executable executable) {
		return new HashKey(executable.getName(), new HashKey(Stream.of(executable.getParameterTypes()).map(Class::getName).toArray(i -> new Object[i])));
	}
	
	private URLClassLoader newClassLoader(Set<URL> urls, ClassLoader parent) {
		return new URLClassLoader(urls.stream().toArray(i -> new URL[i]), parent);
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
