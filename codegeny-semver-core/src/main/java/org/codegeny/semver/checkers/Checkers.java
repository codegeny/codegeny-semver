package org.codegeny.semver.checkers;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Checkers {
	
	static boolean compare(Object previous, Object current) {
		if (previous instanceof Enum<?> && current instanceof Enum<?>) {
			return compareEnum((Enum<?>) previous, (Enum<?>) current);
		}
		if (previous instanceof Class<?> && current instanceof Class<?>) {
			return compareClass((Class<?>) previous, (Class<?>) current);
		}
		if (previous instanceof Annotation && current instanceof Annotation) {
			return compareAnnotation((Annotation) previous, (Annotation) current);
		}
		if (previous instanceof byte[] && current instanceof byte[]) {
			return Arrays.equals((byte[]) previous, (byte[]) current);
		}
		if (previous instanceof char[] && current instanceof char[]) {
			return Arrays.equals((char[]) previous, (char[]) current);
		}
		if (previous instanceof short[] && current instanceof short[]) {
			return Arrays.equals((short[]) previous, (short[]) current);
		}
		if (previous instanceof int[] && current instanceof int[]) {
			return Arrays.equals((int[]) previous, (int[]) current);
		}
		if (previous instanceof long[] && current instanceof long[]) {
			return Arrays.equals((long[]) previous, (long[]) current);
		}
		if (previous instanceof float[] && current instanceof float[]) {
			return Arrays.equals((float[]) previous, (float[]) current);
		}
		if (previous instanceof double[] && current instanceof double[]) {
			return Arrays.equals((double[]) previous, (double[]) current);
		}
		if (previous instanceof boolean[] && current instanceof boolean[]) {
			return Arrays.equals((boolean[]) previous, (boolean[]) current);
		}
		if (previous.getClass().isArray() && current.getClass().isArray() && !previous.getClass().getComponentType().isPrimitive() && !current.getClass().getComponentType().isPrimitive()) {
			return compareArray((Object[]) previous, (Object[]) current);
		}
		return Objects.equals(previous, current);
	}
	
	private static boolean compareAnnotation(Annotation previous, Annotation current) {
		if (!compareClass(previous.annotationType(), current.annotationType())) {
			return false; // not the same type
		}
		Map<String, Method> previousMethods = Stream.of(previous.annotationType().getDeclaredMethods()).collect(toMap(Method::getName, identity()));
		Map<String, Method> currentMethods = Stream.of(current.annotationType().getDeclaredMethods()).collect(toMap(Method::getName, identity()));
		if (!previousMethods.keySet().equals(currentMethods.keySet())) {
			return false; // attributes differ between versions
		}
		for (String name : previousMethods.keySet()) {
			try {
				if (!compare(previousMethods.get(name).invoke(previous), currentMethods.get(name).invoke(current))) {
					return false;
				}
			} catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}
		return true;
	}
	
	private static final Comparator<Class<?>> CLASS_HIERARCHY_COMPARATOR = (a, b) -> a.isAssignableFrom(b)
		? b.isAssignableFrom(a) ? 0 : +1
		: b.isAssignableFrom(a) ? -1 : a.isInterface()
			? b.isInterface() ? 0 : +1
			: b.isInterface() ? -1 : a.getName().compareTo(b.getName());
		
	public static Stream<Class<?>> hierarchy(Class<?> klass) {
		return klass == null ? Stream.empty() : Stream.concat(Stream.of(klass), Stream.concat(hierarchy(klass.getSuperclass()), Stream.of(klass.getInterfaces()).flatMap(Checkers::hierarchy))).distinct().sorted(CLASS_HIERARCHY_COMPARATOR);
	}
	
	private static boolean compareArray(Object[] previous, Object[] current) {
		return previous.length == current.length && IntStream.range(0, previous.length).allMatch(i -> compare(previous[i], current[i]));		
	}
	
	static boolean compareClass(Class<?> left, Class<?> right) {
		return left.getName().equals(right.getName());
	}
	
	private static boolean compareEnum(Enum<?> previous, Enum<?> current) {
		return compareClass(previous.getClass(), current.getClass()) && previous.name().equals(current.name());
	}
	
	private static boolean compareGenericArrayType(GenericArrayType left, GenericArrayType right, Map<TypeVariable<?>, TypeVariable<?>> variables) {
		return compareType(left.getGenericComponentType(), right.getGenericComponentType(), variables);
	}
	
	private static boolean compareGenericDeclaration(GenericDeclaration left, GenericDeclaration right) {
		if (left instanceof Class<?> && right instanceof Class<?>) {
			return compareClass((Class<?>) left, (Class<?>) right);
		}
		return left.getClass().equals(right.getClass());
	}
	
	private static boolean compareParameterizedType(ParameterizedType left, ParameterizedType right, Map<TypeVariable<?>, TypeVariable<?>> variables) {
		return compareType(left.getRawType(), right.getRawType(), variables) && compareType(left.getOwnerType(), right.getOwnerType(), variables) && compareTypes(left.getActualTypeArguments(), right.getActualTypeArguments(), variables);
	}

	// TODO optimize later
	static <T> boolean compareSet(Set<? extends T> previous, Set<? extends T> current, BiPredicate<? super T, ? super T> predicate) {
		return previous.stream().allMatch(a -> current.stream().anyMatch(b -> predicate.test(a, b))) && current.stream().allMatch(a -> previous.stream().anyMatch(b -> predicate.test(a, b)));
	}

	static boolean compareType(Type left, Type right) {
		return compareType(left, right, new HashMap<>());
	}
	
//	static Comparator<Type> newTypeComparator() {
//		Map<TypeVariable<?>, TypeVariable<?>> variables = new HashMap<>();
//		return (a, b) -> compareType(a, b, variables) ? 0 : -1;
//	}
	
	private static boolean compareType(Type left, Type right, Map<TypeVariable<?>, TypeVariable<?>> variables) {
		if (left instanceof Class<?> && right instanceof Class<?>) {
			return compareClass((Class<?>) left, (Class<?>) right);
		}
		if (left instanceof ParameterizedType && right instanceof ParameterizedType) {
			return compareParameterizedType((ParameterizedType) left, (ParameterizedType) right, variables);
		}
		if (left instanceof TypeVariable<?> && right instanceof TypeVariable<?>) {
			return compareTypeVariable((TypeVariable<?>) left, (TypeVariable<?>) right, variables);
		}
		if (left instanceof WildcardType && right instanceof WildcardType) {
			return compareWildcardType((WildcardType) left, (WildcardType) right, variables);
		}
		if (left instanceof GenericArrayType && right instanceof GenericArrayType) {
			return compareGenericArrayType((GenericArrayType) left, (GenericArrayType) right, variables);
		}
		return left == right;
	}
	
	public static Stream<Class<?>> extractClasses(Type type) {
		return extractClasses(type, new HashSet<>());
	}
	
	public static Stream<Type> extractTypesFromClass(Class<?> klass) {
		return Stream.of(
			extractTypesFromGenericDeclaration(klass),
			Stream.of(klass.getGenericSuperclass()),
			Stream.of(klass.getGenericInterfaces()),
			Stream.of(klass.getDeclaredConstructors()).flatMap(Checkers::extractTypesFromConstructor),
			Stream.of(klass.getDeclaredFields()).flatMap(Checkers::extractTypesFromField),
			Stream.of(klass.getDeclaredMethods()).flatMap(Checkers::extractTypesFromMethod)
		).reduce(Stream::concat).orElseGet(Stream::empty).filter(Objects::nonNull);
	}
	
	public static Stream<Type> extractTypesFromField(Field field) {
		return Stream.of(field.getGenericType());
	}
	
	public static Stream<Type> extractTypesFromConstructor(Constructor<?> constructor) {
		return extractTypesFromExecutable(constructor);
	}
	
	public static Stream<Type> extractTypesFromMethod(Method method) {
		return Stream.concat(extractTypesFromExecutable(method), Stream.of(method.getGenericReturnType()));
	}
	
	private static Stream<Type> extractTypesFromExecutable(Executable executable) {
		return Stream.concat(Stream.concat(extractTypesFromGenericDeclaration(executable), Stream.of(executable.getGenericParameterTypes())), Stream.of(executable.getGenericExceptionTypes()));
	}
	
	private static Stream<Type> extractTypesFromGenericDeclaration(GenericDeclaration genericDeclaration) {
		return Stream.of(genericDeclaration.getTypeParameters());
	}
	
	private static Stream<Class<?>> extractClasses(Type type, Set<TypeVariable<?>> variables) {
		if (type instanceof Class<?>) {
			Class<?> klass = (Class<?>) type;
			if (!klass.isPrimitive()) {
				return klass.isArray() ? extractClasses(klass.getComponentType(), variables) : Stream.of(klass);
			}
		}
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			return Stream.concat(
				Stream.of(parameterizedType.getRawType(), parameterizedType.getOwnerType()),
				Stream.of(parameterizedType.getActualTypeArguments())
			).flatMap(t -> extractClasses(t, variables));
		}
		if (type instanceof TypeVariable<?>) {
			TypeVariable<?> typeVariable = (TypeVariable<?>) type;
			if (variables.add(typeVariable)) {
				return Stream.of(typeVariable.getBounds()).flatMap(t -> extractClasses(t, variables));
			}
		}
		if (type instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) type;
			return Stream.concat(
				Stream.of(wildcardType.getLowerBounds()),
				Stream.of(wildcardType.getUpperBounds())
			).flatMap(t -> extractClasses(t, variables));
		}
		if (type instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) type;
			return extractClasses(genericArrayType.getGenericComponentType(), variables);
		}
		return Stream.empty();
	}
	
	static boolean compareTypes(Type[] left, Type[] right) {
		return compareTypes(left, right, new HashMap<>());
	}
	
	private static boolean compareTypes(Type[] left, Type[] right, Map<TypeVariable<?>, TypeVariable<?>> variables) {
		return left.length == right.length && IntStream.range(0, left.length).allMatch(i -> compareType(left[i], right[i], variables));
	}
	
	// TODO still not sure about this
	private static boolean compareTypeVariable(TypeVariable<?> left, TypeVariable<?> right, Map<TypeVariable<?>, TypeVariable<?>> variables) {
		if (position(left) == position(right) && compareGenericDeclaration(left.getGenericDeclaration(), right.getGenericDeclaration())) {
			if (variables.containsKey(left)) {
				return variables.get(left).equals(right);
			}
			variables.put(left, right);
			return compareTypes(left.getBounds(), right.getBounds(), variables);
		}
		return false;
	}
	
	private static boolean compareWildcardType(WildcardType left, WildcardType right, Map<TypeVariable<?>, TypeVariable<?>> variables) {
		return compareTypes(left.getLowerBounds(), right.getLowerBounds(), variables) && compareTypes(left.getUpperBounds(), right.getUpperBounds(), variables);
	}
	
	static boolean enumConstants(Class<?> previous, Class<?> current, BiPredicate<? super List<String>, ? super List<String>> predicate) {
		return notNull(previous, current) && previous.isEnum() && current.isEnum() && predicate.test(enumConstantsOf(previous), enumConstantsOf(current));
	}
	
	static List<String> enumConstantsOf(Class<?> klass) {
		return Stream.of(klass.asSubclass(Enum.class).getEnumConstants()).map(Enum::name).collect(toList());
	}
	
	static boolean fromAnnotations(Method previous, Method current) {
		return notNull(previous, current) && previous.getDeclaringClass().isAnnotation() && current.getDeclaringClass().isAnnotation();
	}
	
	static Set<String> getCheckedExceptionClassNames(Executable executable) {
		return Stream.of(executable.getExceptionTypes()).<Class<? extends Throwable>> map(c -> c.asSubclass(Throwable.class)).filter(Checkers::isChecked).map(Class::getName).collect(toSet());
	}
		
//	static Set<Class<?>> getSuperTypes(Class<?> type, Function<Class<?>, Stream<Class<?>>> extractor) {
//		return type == null ? emptySet() : extractor.apply(type).filter(Objects::nonNull).flatMap(t -> Stream.concat(Stream.of(t), getSuperTypes(t, extractor).stream())).collect(toSet());
//	}
//	
//	static boolean compareTypes(Set<? extends Type> previous, Set<? extends Type> current) {
//		return compareSet(previous, current, (a, b) -> compareType(a, b));
//	}
//	
//	static boolean expandSuperTypes(Type previous, Type current) {
//		return current.stream().anyMatch(c -> previous.stream().noneMatch(p -> compareType(p, c)));
//	}
//	
//	static boolean expandSuperTypes(Set<? extends Type> previous, Set<? extends Type> current) {
//		return current.stream().anyMatch(c -> previous.stream().noneMatch(p -> compareType(p, c)));
//	}
//	
//	static boolean contractSuperTypes(Set<? extends Type> previous, Set<? extends Type> current) {
//		return previous.stream().anyMatch(p -> current.stream().noneMatch(c -> compareType(p, c)));
//	}
	
	static boolean isAbstract(Class<?> klass) {
		return Modifier.isAbstract(klass.getModifiers());
	}
	
	static boolean isAbstract(Member member) {
		return Modifier.isAbstract(member.getModifiers());
	}
	
	static boolean isChecked(Class<? extends Throwable> type) {
		return !RuntimeException.class.isAssignableFrom(type) && !Error.class.isAssignableFrom(type);
	}

	static boolean isFinal(Class<?> klass) {
		return Modifier.isFinal(klass.getModifiers());
	}
		
	static boolean isFinal(Member member) {
		return Modifier.isFinal(member.getModifiers());
	}
	
	static boolean isStatic(Class<?> klass) {
		return Modifier.isStatic(klass.getModifiers());
	}

	static boolean isStatic(Member member) {
		return Modifier.isStatic(member.getModifiers());
	}
	
	static boolean notNull(Object previous, Object current) {
		return previous != null && current != null;
	}
	
	private static int position(TypeVariable<?> typeVariable) {
		return Arrays.asList(typeVariable.getGenericDeclaration().getTypeParameters()).indexOf(typeVariable);
	}
	
	static boolean sameKind(Class<?> previous, Class<?> current) {
		return notNull(previous, current) && Kind.of(previous) == Kind.of(current);
	}
	
	static <T> boolean present(Optional<T> previous, Optional<T> current, BiPredicate<T, T> predicate) {
		return previous.flatMap(p -> current.map(c -> predicate.test(p, c))).orElse(Boolean.FALSE);
	}
}
