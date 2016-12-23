package org.codegeny.semver.checkers;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.codegeny.semver.Change;
import org.codegeny.semver.ClassChangeChecker;
import org.codegeny.semver.Logger;
import org.codegeny.semver.LoggerAware;
import org.codegeny.semver.Metadata;
import org.codegeny.semver.MetadataAware;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class TypeArgumentChecker implements ClassChangeChecker, MetadataAware, LoggerAware {
	
	private Logger logger;
	
	private Metadata metadata;
	
	@Override
	public Change check(Class<?> previous, Class<?> current) {
		logger.log("is public %s %s?", metadata.isPublicAPI(previous), metadata.isPublicAPI(current));
		if (previous == null || current == null) {
			return Change.PATCH;
		}
		if (compareTypes(previous.getTypeParameters(), current.getTypeParameters())) {
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
	
	@Override
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	@Override
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
}
