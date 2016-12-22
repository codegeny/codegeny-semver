package org.codegeny.semver.model;

public class XClassAttribute implements XAttribute {
	
	private final XClass classValue;

	public XClassAttribute(XClass classValue) {
		this.classValue = classValue;
	}

	public XClass getClassValue() {
		return classValue;
	}
	
	@Override
	public <R> R accept(XAttributeVisitor<R> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public String toString() {
		return classValue.getName();
	}
}
