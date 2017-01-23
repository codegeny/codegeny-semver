package org.codegeny.semver;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.codegeny.semver.Change.PATCH;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.codegeny.semver.checkers.Checkers;
import org.codegeny.semver.checkers.ClassCheckers;
import org.codegeny.semver.checkers.ConstructorCheckers;
import org.codegeny.semver.checkers.ExecutableCheckers;
import org.codegeny.semver.checkers.FieldCheckers;
import org.codegeny.semver.checkers.GenericDeclarationCheckers;
import org.codegeny.semver.checkers.MemberCheckers;
import org.codegeny.semver.checkers.MethodCheckers;

public class ModuleChecker {
	
	interface Report<T> {
		
		void report(Change change, String name, T previous, T current);
	}
				
	public static ModuleChecker newConfiguredInstance() {
		
		Set<Metadata> metaSet = new HashSet<>();
		ServiceLoader.load(Metadata.class).forEach(metaSet::add);
		Metadata metadata = metaSet.stream().reduce(Metadata::or).orElseGet(DefaultMetadata::new);
		
		ModuleChecker moduleChangeChecker = new ModuleChecker(metadata);
		
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
	private final Map<String, Checker<? super Method>> methodCheckers = new TreeMap<>();
	private final Metadata metadata;

	public ModuleChecker(Metadata metadata) {
		this.metadata = metadata;
	}

	public Change check(Module previous, Module current, Reporter reporter) {
		
		Set<URL> previousArchives = toURLs(previous.getClassPath());
		Set<URL> currentArchives = toURLs(current.getClassPath());
		
		Set<URL> commonArchives = new HashSet<>(previousArchives);
		commonArchives.retainAll(currentArchives);
		
		previousArchives.removeAll(commonArchives);
		currentArchives.removeAll(commonArchives);
				
		try (
				
			URLClassLoader commonLoader = newClassLoader(commonArchives, Thread.currentThread().getContextClassLoader());
			URLClassLoader previousLoader = newClassLoader(previousArchives, commonLoader);
			URLClassLoader currentLoader = newClassLoader(currentArchives, commonLoader)) {
			
			Set<String> processedClassNames = new HashSet<>();
			previous.getClassNames().forEach(processedClassNames::add);
			current.getClassNames().forEach(processedClassNames::add);
			Queue<String> classNamesToProcess = new LinkedList<>(processedClassNames);
			
			Change globalResult = PATCH;
			
			while (!classNamesToProcess.isEmpty()) {
				
				String className = classNamesToProcess.remove();
				
				Optional<Class<?>> previousClass = getClass(className, previousLoader);
				Optional<Class<?>> currentClass = getClass(className, currentLoader);
				
				if (Objects.equals(previousClass, currentClass)) { // common
					continue;
				}
				
				globalResult = Stream.of(
					check(previousClass, currentClass, classCheckers, metadata::isUsableByClient, reporter::report),
					check(previousClass, currentClass, Class::getDeclaredFields, fieldCheckers, reporter, Field::getName),
					checkMethods(previousClass, currentClass, methodCheckers, reporter),
					check(previousClass, currentClass, Class::getDeclaredConstructors, constructorCheckers,reporter, this::key)
				).reduce(globalResult, Change::combine);
				
				Stream.of(previousClass, currentClass)
					.flatMap(o -> o.map(Checkers::extractTypesFromClass).orElseGet(Stream::empty))
					.flatMap(Checkers::extractClasses)
					.map(Class::getName)
					.filter(n -> !processedClassNames.add(n))
					.forEach(classNamesToProcess::add);
			}
			
			return globalResult;
			
		} catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}
	}
	
	private <T> Change check(Optional<T> previous, Optional<T> current, Map<String, Checker<? super T>> checkers, Predicate<? super T> isPublicAPI, Report<T> report) {
		return !previous.filter(isPublicAPI).isPresent() && !current.filter(isPublicAPI).isPresent() ? PATCH : checkers.entrySet().stream().map(e -> {
			Change change = e.getValue().check(previous.orElse(null), current.orElse(null), metadata);
			report.report(change, e.getKey(), previous.orElse(null), current.orElse(null));
			return change;
		}).reduce(PATCH, Change::combine);
	}
	
	private <T extends Member> Change check(Optional<Class<?>> previous, Optional<Class<?>> current, Function<Class<?>, T[]> extractor, Map<String, Checker<? super T>> checkers, Reporter reporter, Function<? super T, ?> keyer) {
		Map<Object, T> previousMap = previous.map(extractor).map(Stream::of).orElseGet(Stream::empty).filter(m -> !m.isSynthetic()).collect(toMap(keyer, identity()));
		Map<Object, T> currentMap = current.map(extractor).map(Stream::of).orElseGet(Stream::empty).filter(m -> !m.isSynthetic()).collect(toMap(keyer, identity()));
		return Stream.concat(previousMap.keySet().stream(), currentMap.keySet().stream()).distinct().map(k -> {
			Optional<T> p = Optional.ofNullable(previousMap.get(k));
			Optional<T> c =  Optional.ofNullable(currentMap.get(k));
			return previous.equals(current) ? PATCH : check(p, c, checkers, metadata::isUsableByClient, reporter::report);
		}).reduce(PATCH, Change::combine);
	}
	
	private Change checkMethods(Optional<Class<?>> previous, Optional<Class<?>> current, Map<String, Checker<? super Method>> checkers, Reporter reporter) {
		Set<Object> previousMap = previous.map(Class::getDeclaredMethods).map(Stream::of).orElseGet(Stream::empty).filter(m -> !m.isSynthetic()).map(this::key).collect(toSet());
		Set<Object> currentMap = current.map(Class::getDeclaredMethods).map(Stream::of).orElseGet(Stream::empty).filter(m -> !m.isSynthetic()).map(this::key).collect(toSet());
		return Stream.concat(previousMap.stream(), currentMap.stream()).distinct().map(k -> {
			Optional<Method> p = previous
				.map(Checkers::hierarchy)
				.orElseGet(Stream::empty)
				.map(Class::getDeclaredMethods)
				.flatMap(Stream::of)
				.filter(m -> !m.isSynthetic())
				.filter(m -> k.equals(key(m)))
				.findFirst();
			Optional<Method> c = current
				.map(Checkers::hierarchy)
				.orElseGet(Stream::empty)
				.map(Class::getDeclaredMethods)
				.flatMap(Stream::of)
				.filter(m -> !m.isSynthetic())
				.filter(m -> k.equals(key(m)))
				.findFirst();
			return previous.equals(current) ? PATCH : check(p, c, checkers, metadata::isUsableByClient, reporter::report);
		}).reduce(PATCH, Change::combine);
	}

	private Optional<Class<?>> getClass(String className, ClassLoader classLoader) {
		try {
			return Optional.of(classLoader.loadClass(className));
		} catch (ClassNotFoundException classNotFoundException) {
			return Optional.empty();
		}
	}
	
	private Object key(Executable executable) {
		return Stream.concat(Stream.of(executable.getName()), Stream.of(executable.getParameterTypes()).map(Class::getName)).collect(toList());
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
