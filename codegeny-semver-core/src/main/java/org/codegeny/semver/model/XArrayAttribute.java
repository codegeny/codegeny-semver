package org.codegeny.semver.model;

import static java.util.stream.Collectors.joining;

import java.util.List;

public class XArrayAttribute implements XAttribute {
	
	private final List<XAttribute> values;

	public XArrayAttribute(List<XAttribute> values) {
		this.values = values;
	}
	
	public List<XAttribute> getValues() {
		return values;
	}
	
	@Override
	public <R> R accept(XAttributeVisitor<R> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public String toString() {
		return values.stream().map(Object::toString).collect(joining(", ", "{", "}"));
	}
}
