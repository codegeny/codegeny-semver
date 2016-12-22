package org.codegeny.semver.model;

public interface XTypeVisitor<R> {
	
	R visit(XArrayType arrayType);
	
	R visit(XClassType classType);
	
	R visit(XTypeVariable typeVariable);
	
	R visit(XBasicType basicType);
}
