package org.codegeny.semver.model;

public class XPrimitiveAttribute  implements XAttribute {

	private final XClass primitiveClass;
	private final Object primitiveValue;
	
	public XPrimitiveAttribute(XClass primitiveClass, Object primitiveValue) {
		this.primitiveClass = primitiveClass;
		this.primitiveValue = primitiveValue;
	}
	
	@Override
	public <R> R accept(XAttributeVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public XClass getPrimitiveClass() {
		return primitiveClass;
	}
	
	public Object getPrimitiveValue() {
		return primitiveValue;
	}
	
	@Override
	public String toString() {
		return primitiveValue.toString();
	}
}
