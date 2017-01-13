package org.codegeny.semver.checkers;

import static org.codegeny.semver.Change.MAJOR;
import static org.codegeny.semver.Change.MINOR;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

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
			return MAJOR.when(notNull(previous, current) && current.getTypeParameters().length == previous.getTypeParameters().length && !compareTypes(previous.getTypeParameters(), current.getTypeParameters(), new HashMap<>()));
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
	
	boolean compareGenericArrayType(GenericArrayType left, GenericArrayType right, Map<TypeVariable<?>, TypeVariable<?>> variables) {
		return compareType(left.getGenericComponentType(), right.getGenericComponentType(), variables);
	}
	
	boolean compareGenericDeclaration(GenericDeclaration left, GenericDeclaration right) {
		if (left instanceof Class<?> && right instanceof Class<?>) {
			return compareClass((Class<?>) left, (Class<?>) right);
		}
		return left.getClass().equals(right.getClass());
	}
	
	boolean compareParameterizedType(ParameterizedType left, ParameterizedType right, Map<TypeVariable<?>, TypeVariable<?>> variables) {
		return compareType(left.getRawType(), right.getRawType(), variables)
			&& compareType(left.getOwnerType(), right.getOwnerType(), variables)
			&& compareTypes(left.getActualTypeArguments(), right.getActualTypeArguments(), variables);
	}
	
	boolean compareType(Type left, Type right, Map<TypeVariable<?>, TypeVariable<?>> variables) {
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
	
	boolean compareTypes(Type[] left, Type[] right, Map<TypeVariable<?>, TypeVariable<?>> variables) {
		return left.length == right.length && IntStream.range(0, left.length).allMatch(i -> compareType(left[i], right[i], variables));
	}
	
	// TODO still not sure about this
	boolean compareTypeVariable(TypeVariable<?> left, TypeVariable<?> right, Map<TypeVariable<?>, TypeVariable<?>> variables) {
		if (position(left) == position(right) && compareGenericDeclaration(left.getGenericDeclaration(), right.getGenericDeclaration())) {
			if (variables.containsKey(left)) {
				return variables.get(left).equals(right);
			}
			variables.put(left, right);
			return compareTypes(left.getBounds(), right.getBounds(), variables);
		}
		return false;
	}
	
	boolean compareWildcardType(WildcardType left, WildcardType right, Map<TypeVariable<?>, TypeVariable<?>> variables) {
		return compareTypes(left.getLowerBounds(), right.getLowerBounds(), variables) && compareTypes(left.getUpperBounds(), right.getUpperBounds(), variables);
	}
	
	boolean notNull(Object previous, Object current) {
		return previous != null && current != null;
	}
	
	int position(TypeVariable<?> typeVariable) {
		return Arrays.asList(typeVariable.getGenericDeclaration().getTypeParameters()).indexOf(typeVariable);
	}
}
