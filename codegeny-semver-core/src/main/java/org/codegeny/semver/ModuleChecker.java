package org.codegeny.semver;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.codegeny.semver.checkers.ClassCheckers;
import org.codegeny.semver.checkers.ConstructorCheckers;
import org.codegeny.semver.checkers.ExecutableCheckers;
import org.codegeny.semver.checkers.FieldCheckers;
import org.codegeny.semver.checkers.GenericDeclarationCheckers;
import org.codegeny.semver.checkers.MemberCheckers;
import org.codegeny.semver.checkers.MethodCheckers;

public class ModuleChecker implements Checker<Module> {
	
	private static class Default implements Metadata {
		
		public boolean isImplementedByClient(Class<?> klass) {
			return !Modifier.isFinal(klass.getModifiers());
		}
		
		public boolean isImplementedByClient(Method method) {
			return isImplementedByClient(method.getDeclaringClass()) && !Modifier.isFinal(method.getModifiers());
		}
		
		public boolean isPublicAPI(Class<?> klass) {
			return Modifier.isPublic(klass.getModifiers());
		}
		
		public boolean isPublicAPI(Member member) {
			return isPublicAPI(member.getDeclaringClass()) && !Modifier.isPrivate(member.getModifiers());
		}	
	}
		
	public static ModuleChecker newConfiguredInstance(Logger logger) {
		
		Set<Metadata> metaSet = new HashSet<>();
		ServiceLoader.load(Metadata.class).forEach(metaSet::add);
		Metadata metadata = metaSet.stream().reduce(Metadata::or).orElseGet(Default::new);
		
		ModuleChecker moduleChangeChecker = new ModuleChecker(logger, metadata);
		
		register(ClassCheckers.class, moduleChangeChecker::registerClassChecker);
		register(ConstructorCheckers.class, moduleChangeChecker::registerConstructorChecker);
		register(FieldCheckers.class, moduleChangeChecker::registerFieldChecker);
		register(MethodCheckers.class, moduleChangeChecker::registerMethodChecker);
		register(ExecutableCheckers.class, moduleChangeChecker::registerExecutableChecker);
		register(GenericDeclarationCheckers.class, moduleChangeChecker::registerGenericDeclarationChecker);
		register(MemberCheckers.class, moduleChangeChecker::registerMemberChecker);
		
		return moduleChangeChecker;
	}
	
	private static <T extends Enum<T>> void register(Class<T> enumClass, BiConsumer<String, T> registry) {
		Stream.of(enumClass.getEnumConstants()).forEach(e -> registry.accept(e.name(), e));
	}
	
	private final Map<String, Checker<? super Class<?>>> classCheckers = new TreeMap<>();
	private final Map<String, Checker<? super Constructor<?>>> constructorCheckers = new TreeMap<>();
	private final Map<String, Checker<? super Field>> fieldCheckers = new TreeMap<>();
	private final Logger logger;
	private final Metadata metadata;
	private final Map<String, Checker<? super Method>> methodCheckers = new TreeMap<>();

	public ModuleChecker(Logger logger, Metadata metadata) {
		this.logger = logger;
		this.metadata = metadata;
	}

	@Override
	public Change check(Module previous, Module current) {
		
		Set<URL> previousArchives = toURLs(previous.getClassPath());
		Set<URL> currentArchives = toURLs(current.getClassPath());
		
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
				
				if (previousClass == currentClass) { // common
					continue;
				}
				
				List<Supplier<Change>> checkers = new LinkedList<>();
				checkers.add(() -> check(previousClass, currentClass, classCheckers, metadata::isPublicAPI));
				checkers.add(() -> check(previousClass.getDeclaredFields(), currentClass.getDeclaredFields(), fieldCheckers, metadata::isPublicAPI, Field::getName));
				checkers.add(() -> check(previousClass.getDeclaredMethods(), currentClass.getDeclaredMethods(), methodCheckers, metadata::isPublicAPI, this::key));
				checkers.add(() -> check(previousClass.getDeclaredConstructors(), currentClass.getDeclaredConstructors(), constructorCheckers, metadata::isPublicAPI, this::key));
				
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
	
	private <T> Change check(T previous, T current, Map<String, Checker<? super T>> checkers, Predicate<? super T> isPublicAPI) {
		Change result = Change.PATCH;
		if (previous != null && !isPublicAPI.test(previous) && current != null && !isPublicAPI.test(current)) {
			return result;
		}
		logger.log("+ %s", previous != null ? previous : current);
		for (Map.Entry<String, Checker<? super T>> checker : checkers.entrySet()) {
			Change change = checker.getValue().check(previous, current);
			logger.log("  - %s : %s", change, checker.getKey());
			result = result.combine(change);
			if (result == Change.MAJOR) {
				break;
			}
		}
		logger.log("= %s", result);
		return result;
	}
	
	private <T> Change check(T[] previouses, T[] currents, Map<String, Checker<? super T>> checkers, Predicate<? super T> isPublicAPI, Function<? super T, ?> keyer) {
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
		return new HashKey(executable.getName(), new HashKey(Stream.of(executable.getParameterTypes()).map(Class::getName)));
	}
	
	private URLClassLoader newClassLoader(Set<URL> urls, ClassLoader parent) {
		return new URLClassLoader(urls.stream().toArray(i -> new URL[i]), parent);
	}
	
	public void registerClassChecker(String name, Checker<? super Class<?>> classChangeChecker) {
		this.classCheckers.put(name, classChangeChecker);
	}
	
	public void registerConstructorChecker(String name, Checker<? super Constructor<?>> constructorChangeChecker) {
		this.constructorCheckers.put(name, constructorChangeChecker);
	}
	
	public void registerExecutableChecker(String name, Checker<? super Executable> checker) {
		registerConstructorChecker(name, checker);
		registerMethodChecker(name, checker);
	}
	
	public void registerFieldChecker(String name, Checker<? super Field> fieldChangeChecker) {
		this.fieldCheckers.put(name, fieldChangeChecker);
	}
	
	public void registerGenericDeclarationChecker(String name, Checker<? super GenericDeclaration> checker) {
		registerClassChecker(name, checker);
		registerMethodChecker(name, checker);
	}
	
	public void registerMemberChecker(String name, Checker<? super Member> checker) {
		registerExecutableChecker(name, checker);
		registerFieldChecker(name, checker);
	}
	
	public void registerMethodChecker(String name, Checker<? super Method> methodChangeChecker) {
		this.methodCheckers.put(name, methodChangeChecker);
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
