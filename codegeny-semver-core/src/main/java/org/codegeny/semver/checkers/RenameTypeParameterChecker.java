package org.codegeny.semver.checkers;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.codegeny.semver.Change;
import org.codegeny.semver.GenericDeclarationChangeChecker;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class RenameTypeParameterChecker implements GenericDeclarationChangeChecker {
	
	@Override
	public Change check(GenericDeclaration previous, GenericDeclaration current) {
		if (previous == null || current == null) {
			return Change.PATCH;
		}
		Type[] previousTypeParameters = previous.getTypeParameters();
		Type[] currentTypeParameters = current.getTypeParameters();
		if (previousTypeParameters.length != currentTypeParameters.length) {
			return Change.PATCH;
		}
		if (compareTypes(previousTypeParameters, currentTypeParameters)) {
			return Change.PATCH;
		}
		return Change.MAJOR;
	}
	
	private boolean compareType(Type leftType, Type rightType) {
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
				&& compareTypes(leftTypeVariable.getBounds(), rightTypeVariable.getBounds());
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
	
	private boolean compareTypes(Type[] leftTypes, Type[] rightTypes) {
		return leftTypes.length == rightTypes.length
			&& IntStream.range(0, leftTypes.length).allMatch(i -> compareType(leftTypes[i], rightTypes[i]));
	}
	
	private int position(TypeVariable<?> typeVariable) {
		return Stream.of(typeVariable.getGenericDeclaration().getTypeParameters()).map(TypeVariable::getName).collect(Collectors.toList()).indexOf(typeVariable.getName());
	}
}
