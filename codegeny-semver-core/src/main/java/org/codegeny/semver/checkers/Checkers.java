package org.codegeny.semver.checkers;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class Checkers {
	
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
			return false;
		}
		Map<String, Method> previousMethods = Stream.of(previous.annotationType().getDeclaredMethods()).collect(toMap(Method::getName, identity()));
		Map<String, Method> currentMethods = Stream.of(current.annotationType().getDeclaredMethods()).collect(toMap(Method::getName, identity()));
		if (!previousMethods.keySet().equals(currentMethods.keySet())) {
			return false;
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
		return compareType(left.getRawType(), right.getRawType(), variables)
			&& compareType(left.getOwnerType(), right.getOwnerType(), variables)
			&& compareTypes(left.getActualTypeArguments(), right.getActualTypeArguments(), variables);
	}

	static boolean compareType(Type left, Type right) {
		return compareType(left, right, new HashMap<>());
	}

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
		return Stream.of(executable.getExceptionTypes()).filter(Checkers::isChecked).map(Class::getName).collect(toSet());
	}
	
	static boolean isAbstract(Class<?> klass) {
		return Modifier.isAbstract(klass.getModifiers());
	}
		
	static boolean isAbstract(Member member) {
		return Modifier.isAbstract(member.getModifiers());
	}
	
	static boolean isChecked(Class<?> type) {
		return !RuntimeException.class.isAssignableFrom(type) && !Error.class.isAssignableFrom(type);
	}
	
	static boolean isFinal(Class<?> klass) {
		return Modifier.isFinal(klass.getModifiers());
	}

	static boolean isFinal(Member member) {
		return Modifier.isFinal(member.getModifiers());
	}
		
	static boolean isStatic(Member member) {
		return Modifier.isStatic(member.getModifiers());
	}
	
	static boolean isStatic(Class<?> klass) {
		return Modifier.isStatic(klass.getModifiers());
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
}
