package org.codegeny.semver.model;

public class XStringAttribute implements XAttribute {
	
	private final String value;

	public XStringAttribute(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	@Override
	public <R> R accept(XAttributeVisitor<R> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public String toString() {
		return new StringBuilder("\"").append(value).append("\"").toString();
	}
}
