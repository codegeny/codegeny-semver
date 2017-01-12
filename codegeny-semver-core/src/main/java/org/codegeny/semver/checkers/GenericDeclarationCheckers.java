package org.codegeny.semver.checkers;

import static java.util.stream.Collectors.toList;
import static org.codegeny.semver.Change.MAJOR;
import static org.codegeny.semver.Change.MINOR;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
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
	DELETE_TYPE_PARAMETER {
		
		@Override
		public Change check(GenericDeclaration previous, GenericDeclaration current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && current.getTypeParameters().length < previous.getTypeParameters().length);
		}
	},
	CHANGE_TYPE_PARAMETERS_BOUNDS {
		
		@Override
		public Change check(GenericDeclaration previous, GenericDeclaration current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && current.getTypeParameters().length == previous.getTypeParameters().length && !compareTypes(previous.getTypeParameters(), current.getTypeParameters()));
		}
	};
	
	boolean compareGenericDeclaration(GenericDeclaration left, GenericDeclaration right) {
		if (left instanceof Type && right instanceof Type) {
			return compareType((Type) left, (Type) right);
		}
		return left.getClass().equals(right.getClass()); // methods and constructors
	}
	
	boolean compareType(Type leftType, Type rightType) {
		if (leftType instanceof Class<?> && rightType instanceof Class<?>) {
			Class<?> leftClass = (Class<?>) leftType;
			Class<?> rightClass = (Class<?>) rightType;
			return leftClass.getName().equals(rightClass.getName());
		}
		if (leftType instanceof ParameterizedType && rightType instanceof ParameterizedType) {
			ParameterizedType leftParameterizedType = (ParameterizedType) leftType;
			ParameterizedType rightParameterizedType = (ParameterizedType) rightType;
			return compareType(leftParameterizedType.getRawType(), rightParameterizedType.getRawType())
				&& compareType(leftParameterizedType.getOwnerType(), rightParameterizedType.getOwnerType())
				&& compareTypes(leftParameterizedType.getActualTypeArguments(), rightParameterizedType.getActualTypeArguments());
		}
		if (leftType instanceof TypeVariable<?> && rightType instanceof TypeVariable<?>) {
			TypeVariable<?> leftTypeVariable = (TypeVariable<?>) leftType;
			TypeVariable<?> rightTypeVariable = (TypeVariable<?>) rightType;
			return compareType(leftTypeVariable.getGenericDeclaration().getClass(), rightTypeVariable.getGenericDeclaration().getClass())
				&& position(leftTypeVariable) == position(rightTypeVariable)
				&& compareTypes(leftTypeVariable.getBounds(), rightTypeVariable.getBounds())
				&& compareGenericDeclaration(leftTypeVariable.getGenericDeclaration(), rightTypeVariable.getGenericDeclaration());
		}
		if (leftType instanceof WildcardType && rightType instanceof WildcardType) {
			WildcardType leftWildcardType = (WildcardType) leftType;
			WildcardType rightWildcardType = (WildcardType) rightType;
			return compareTypes(leftWildcardType.getLowerBounds(), rightWildcardType.getLowerBounds())
				&& compareTypes(leftWildcardType.getUpperBounds(), rightWildcardType.getUpperBounds());
		}
		if (leftType instanceof GenericArrayType && rightType instanceof GenericArrayType) {
			GenericArrayType leftGenericArrayType = (GenericArrayType) leftType;
			GenericArrayType rightGenericArrayType = (GenericArrayType) rightType;
			return compareType(leftGenericArrayType.getGenericComponentType(), rightGenericArrayType.getGenericComponentType());
		}
		return leftType == null && rightType == null;
	}
	
	boolean compareTypes(Type[] leftTypes, Type[] rightTypes) {
		return leftTypes.length == rightTypes.length && IntStream.range(0, leftTypes.length).allMatch(i -> compareType(leftTypes[i], rightTypes[i]));
	}
	
	boolean notNull(Object previous, Object current) {
		return previous != null && current != null;
	}
	
	int position(TypeVariable<?> typeVariable) {
		return Stream.of(typeVariable.getGenericDeclaration().getTypeParameters()).map(TypeVariable::getName).collect(toList()).indexOf(typeVariable.getName());
	}
}
