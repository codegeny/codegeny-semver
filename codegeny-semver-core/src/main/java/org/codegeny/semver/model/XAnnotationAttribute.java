package org.codegeny.semver.model;

public class XAnnotationAttribute implements XAttribute {
	
	private final XAnnotation annotation;

	public XAnnotationAttribute(XAnnotation annotation) {
		this.annotation = annotation;
	}
	
	public XAnnotation getAnnotation() {
		return annotation;
	}
	
	@Override
	public <R> R accept(XAttributeVisitor<R> visitor) {
		return visitor.visit(this);
	}
}
