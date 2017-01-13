package org.codegeny.semver.checkers;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.codegeny.semver.Change.MAJOR;
import static org.codegeny.semver.Change.MINOR;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.codegeny.semver.Change;
import org.codegeny.semver.Checker;
import org.codegeny.semver.Metadata;

public enum MethodCheckers implements Checker<Method> {
	
	ADD_ANNOTATION_ELEMENT_WITH_DEFAULT_VALUE {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MINOR.when(previous == null && current != null && current.getDeclaringClass().isAnnotation() && current.getDefaultValue() != null);
		}
	},
	ADD_ANNOTATION_ELEMENT_WITHOUT_DEFAULT_VALUE {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MAJOR.when(previous == null && current != null && current.getDeclaringClass().isAnnotation() && current.getDefaultValue() == null);
		}
	},
	ADD_DEFAULT_CLAUSE {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MINOR.when(fromAnnotations(previous, current) && previous.getDefaultValue() == null && current.getDefaultValue() != null);
		}
	},
	ADD_DEFAULT_METHOD_IMPLEMENTABLE_BY_CLIENT {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MAJOR.when(previous == null && current != null && current.isDefault() && metadata.isImplementedByClient(current.getDeclaringClass()));
		}
	},
	ADD_DEFAULT_METHOD_NOT_IMPLEMENTABLE_BY_CLIENT {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MINOR.when(previous == null && current != null && current.isDefault() && !metadata.isImplementedByClient(current.getDeclaringClass()));
		}
	},
	ADD_NON_DEFAULT_METHOD_IMPLEMENTABLE_BY_CLIENT {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MAJOR.when(previous == null && current != null && !current.getDeclaringClass().isAnnotation() && !current.isDefault() && !isStatic(current) && metadata.isImplementedByClient(current));
		}
	},
	ADD_NON_DEFAULT_METHOD_NOT_IMPLEMENTABLE_BY_CLIENT {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MINOR.when(previous == null && current != null && !current.getDeclaringClass().isAnnotation() && !current.isDefault() && !isStatic(current) && !metadata.isImplementedByClient(current));
		}
	},
	ADD_STATIC_METHOD {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MINOR.when(previous == null && current != null && isStatic(current));
		}		
	},
	CHANGE_ABSTRACT_TO_NON_ABSTRACT {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MINOR.when(notNull(previous, current) && isAbstract(previous) && !isAbstract(current) && !isStatic(current));
		}
	},
	CHANGE_DEFAULT_CLAUSE {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			// TODO Objects.equals() is not sufficient (what about Class attributes?)
			return MINOR.when(fromAnnotations(previous, current) && previous.getReturnType().getName().equals(current.getReturnType().getName()) && notNull(previous.getDefaultValue(), current.getDefaultValue()) && !compare(previous.getDefaultValue(), current.getDefaultValue()));
		}
	},
	CHANGE_FINAL_TO_NON_FINAL {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MINOR.when(notNull(previous, current) && isFinal(previous) && !isFinal(current));
		}
	},
	CHANGE_NON_ABSTRACT_TO_ABSTRACT {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && !isAbstract(previous) && isAbstract(current));
		}
	},
	CHANGE_NON_FINAL_TO_FINAL {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			// TODO implementable by client
			return MAJOR.when(notNull(previous, current) && !isFinal(previous) && isFinal(current));
		}
	},
	CHANGE_RESULT_TYPE {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && !previous.getReturnType().getName().equals(current.getReturnType().getName()));
		}
	},
	REMOVE_DEFAULT_CLAUSE {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MAJOR.when(fromAnnotations(previous, current) && previous.getDefaultValue() != null && current.getDefaultValue() == null);
		}
	};
		
	boolean compare(Object previous, Object current) {
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
	
	boolean compareAnnotation(Annotation previous, Annotation current) {
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

	boolean compareArray(Object[] previous, Object[] current) {
		return previous.length == current.length && IntStream.range(0, previous.length).allMatch(i -> compare(previous[i], current[i]));		
	}
	
	boolean compareClass(Class<?> previous, Class<?> current) {
		return previous.getName().equals(current.getName());
	}
	
	boolean compareEnum(Enum<?> previous, Enum<?> current) {
		return compareClass(previous.getClass(), current.getClass()) && previous.name().equals(current.name());
	}	
	boolean fromAnnotations(Method previous, Method current) {
		return notNull(previous, current) && previous.getDeclaringClass().isAnnotation() && current.getDeclaringClass().isAnnotation();
	}
	
	boolean isAbstract(Method method) {
		return Modifier.isAbstract(method.getModifiers());
	}
	
	boolean isFinal(Method method) {
		return Modifier.isFinal(method.getModifiers());
	}
	
	boolean isStatic(Method method) {
		return Modifier.isStatic(method.getModifiers());
	}
	
	boolean notNull(Object previous, Object current) {
		return previous != null && current != null;
	}
}
