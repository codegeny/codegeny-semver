package org.codegeny.semver.model;

public class XEnumAttribute implements XAttribute {

	private final XClass enumClass;
	private final String enumValue;
	
	public XEnumAttribute(XClass enumClass, String enumValue) {
		this.enumClass = enumClass;
		this.enumValue = enumValue;
	}
	
	@Override
	public <R> R accept(XAttributeVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public XClass getEnumClass() {
		return enumClass;
	}
	
	public String getEnumValue() {
		return enumValue;
	}
	
	@Override
	public String toString() {
		return enumValue;
	}
}
