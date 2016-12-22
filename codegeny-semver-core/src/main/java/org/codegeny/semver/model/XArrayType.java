package org.codegeny.semver.model;

public class XArrayType implements XType {
	
	private final XType componentType;
	
	XArrayType(XType componentType) {
		this.componentType = componentType;
	}

	public XType getComponentType() {
		return componentType;
	}
	
	@Override
	public <R> R accept(XTypeVisitor<R> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public String toString() {
		return componentType.toString().concat("[]");
	}
	
	@Override
	public int hashCode() {
		return componentType.hashCode();
	}
	
	@Override
	public boolean equals(Object that) {
		return super.equals(that) || that instanceof XArrayType && componentType.equals(((XArrayType) that).componentType);
	}
}
