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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ModuleChangeChecker implements ChangeChecker<Module> {
		
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
		
		previousDependencies.add(toURL(previous.getMain()));
		currentDependencies.add(toURL(current.getMain()));
		
		try (
				
			URLClassLoader commonLoader = new URLClassLoader(commonDependencies.stream().toArray(i -> new URL[i]), Thread.currentThread().getContextClassLoader());
			URLClassLoader previousLoader = new URLClassLoader(previousDependencies.stream().toArray(i -> new URL[i]), commonLoader);
			URLClassLoader currentLoader = new URLClassLoader(currentDependencies.stream().toArray(i -> new URL[i]), commonLoader)) {
			
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
				
				AtomicInteger counter = new AtomicInteger();
				
				List<Supplier<Change>> checkers = new LinkedList<>();
				checkers.add(() -> check(previousClass, currentClass, classChangeCheckers, counter));
				checkers.add(() -> check(previousClass.getDeclaredFields(), currentClass.getDeclaredFields(), fieldChangeCheckers, counter, Field::getName));
				checkers.add(() -> check(previousClass.getDeclaredMethods(), currentClass.getDeclaredMethods(), methodChangeCheckers, counter, this::key));
				checkers.add(() -> check(previousClass.getDeclaredConstructors(), currentClass.getDeclaredConstructors(), constructorChangeCheckers, counter, this::key));
				
				Change classResult = combine(checkers);
				
//				logger.log("%s :: %s (%d)", classResult, className, counter.get());
				
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
	
	private <T> Change check(T previous, T current, Iterable<ChangeChecker<? super T>> checkers, AtomicInteger counter) {
		Change result = Change.PATCH;
		logger.log("+ %s", previous);
		logger.log("+ %s", current);
		for (ChangeChecker<? super T> checker : checkers) {
			counter.incrementAndGet();
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
	
	private <T> Change check(T[] previouses, T[] currents,  Iterable<ChangeChecker<? super T>> checkers,  AtomicInteger counter, Function<T, Object> keyer) {
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
			result = result.combine(check(previous, current, checkers, counter));
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
	
	private Class<?> getClass(String className, ClassLoader realm) {
		try {
			return realm.loadClass(className);
		} catch (ClassNotFoundException classNotFoundException) {
			return null;
		}
	}
	
	private Object key(Executable executable) {
		return new HashKey(Stream.of(executable.getParameterTypes()).map(Class::getName).toArray(i -> new Object[i]));
	}
	
	private Object key(Method method) {
		return new HashKey(method.getName(), key((Executable) method));
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
