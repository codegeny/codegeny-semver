package org.codegeny.semver.checkers;

import static java.util.stream.Collectors.toList;
import static org.codegeny.semver.Change.MAJOR;
import static org.codegeny.semver.Change.MINOR;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.codegeny.semver.Change;
import org.codegeny.semver.Checker;
import org.codegeny.semver.Metadata;

public enum GenericDeclarationCheckers implements Checker<GenericDeclaration> {
	
	ADD_TYPE_PARAMETER_WHEN_NO_PARAMETERS_EXIST {
		
		@Override
		public Change check(GenericDeclaration previous, GenericDeclaration current, Metadata metadata) {
			return MINOR.when(notNull(previous, current) && current.getTypeParameters().length > previous.getTypeParameters().length && previous.getTypeParameters().length == 0);
		}
	},
	ADD_TYPE_PARAMETER_WHEN_OTHER_PARAMETERS_EXIST {
		
		@Override
		public Change check(GenericDeclaration previous, GenericDeclaration current, Metadata metadata) {
			return MINOR.when(notNull(previous, current) && current.getTypeParameters().length > previous.getTypeParameters().length && previous.getTypeParameters().length > 0);
		}
	},
	CHANGE_TYPE_PARAMETERS_BOUNDS {
		
		@Override
		public Change check(GenericDeclaration previous, GenericDeclaration current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && current.getTypeParameters().length == previous.getTypeParameters().length && !compareTypes(previous.getTypeParameters(), current.getTypeParameters(), new HashSet<>(), new HashSet<>()));
		}
	},
	DELETE_TYPE_PARAMETER {
		
		@Override
		public Change check(GenericDeclaration previous, GenericDeclaration current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && current.getTypeParameters().length < previous.getTypeParameters().length);
		}
	};
	
	boolean compareClass(Class<?> left, Class<?> right) {
		return left.getName().equals(right.getName());
	}
	
	boolean compareGenericArrayType(GenericArrayType left, GenericArrayType right, Set<String> leftVariables, Set<String> rightVariables) {
		return compareType(left.getGenericComponentType(), right.getGenericComponentType(), leftVariables, rightVariables);
	}
	
	boolean compareGenericDeclaration(GenericDeclaration left, GenericDeclaration right) {
		if (left instanceof Class<?> && right instanceof Class<?>) {
			return compareClass((Class<?>) left, (Class<?>) right);
		}
		if (left instanceof Constructor<?> && right instanceof Constructor<?>) {
			return true;
		}
		if (left instanceof Method && right instanceof Method) {
			return true;
		}
		return false;
	}
	
	boolean compareParameterizedType(ParameterizedType left, ParameterizedType right, Set<String> leftVariables, Set<String> rightVariables) {
		return compareType(left.getRawType(), right.getRawType(), leftVariables, rightVariables)
			&& compareType(left.getOwnerType(), right.getOwnerType(), leftVariables, rightVariables)
			&& compareTypes(left.getActualTypeArguments(), right.getActualTypeArguments(), leftVariables, rightVariables);
	}
	
	boolean compareType(Type left, Type right, Set<String> leftVariables, Set<String> rightVariables) {
		if (left instanceof Class<?> && right instanceof Class<?>) {
			return compareClass((Class<?>) left, (Class<?>) right);
		}
		if (left instanceof ParameterizedType && right instanceof ParameterizedType) {
			return compareParameterizedType((ParameterizedType) left, (ParameterizedType) right, leftVariables, rightVariables);
		}
		if (left instanceof TypeVariable<?> && right instanceof TypeVariable<?>) {
			return compareTypeVariable((TypeVariable<?>) left, (TypeVariable<?>) right, leftVariables, rightVariables);
		}
		if (left instanceof WildcardType && right instanceof WildcardType) {
			return compareWildcardType((WildcardType) left, (WildcardType) right, leftVariables, rightVariables);
		}
		if (left instanceof GenericArrayType && right instanceof GenericArrayType) {
			return compareGenericArrayType((GenericArrayType) left, (GenericArrayType) right, leftVariables, rightVariables);
		}
		return left == null && right == null;
	}
	
	boolean compareTypes(Type[] left, Type[] right, Set<String> leftVariables, Set<String> rightVariables) {
		return left.length == right.length && IntStream.range(0, left.length).allMatch(i -> compareType(left[i], right[i], leftVariables, rightVariables));
	}
	
	// TODO still not sure about this
	boolean compareTypeVariable(TypeVariable<?> left, TypeVariable<?> right, Set<String> leftVariables, Set<String> rightVariables) {
		if (position(left) == position(right) && compareGenericDeclaration(left.getGenericDeclaration(), right.getGenericDeclaration())) {
			if (leftVariables.contains(left.getName()) && rightVariables.contains(right.getName())) {
				return true;
			}
			leftVariables.add(left.getName()); // what happens if a method type variable hides a class type variable?
			rightVariables.add(right.getName());
			return compareTypes(left.getBounds(), right.getBounds(), leftVariables, rightVariables);
		}
		return false;
	}
	
	boolean compareWildcardType(WildcardType left, WildcardType right, Set<String> leftVariables, Set<String> rightVariables) {
		return compareTypes(left.getLowerBounds(), right.getLowerBounds(), leftVariables, rightVariables) && compareTypes(left.getUpperBounds(), right.getUpperBounds(), leftVariables, rightVariables);
	}
	
	boolean notNull(Object previous, Object current) {
		return previous != null && current != null;
	}
	
	int position(TypeVariable<?> typeVariable) {
		return Stream.of(typeVariable.getGenericDeclaration().getTypeParameters()).map(TypeVariable::getName).collect(toList()).indexOf(typeVariable.getName());
	}
}
