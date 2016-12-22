package org.codegeny.semver.model;

public interface XAttributeVisitor<R> {
	
	R visit(XAnnotationAttribute annotationAttribute);
	
	R visit(XClassAttribute classAttribute);
	
	R visit(XEnumAttribute enumAttribute);
	
	R visit(XPrimitiveAttribute primitiveAttribute);
	
	R visit(XStringAttribute stringAttribute);
	
	R visit(XArrayAttribute arrayAttribute);
}
