package org.codegeny.semver.model;

public enum XBasicType implements XType {

	BOOLEAN("java.lang.Boolean"),
	BYTE("java.lang.Byte"),
	CHAR("java.lang.Character"),
	DOUBLE("java.lang.Double"),
	FLOAT("java.lang.Float"),
	INT("java.lang.Integer"),
	LONG("java.lang.Long"),
	SHORT("java.lang.Short"),
	VOID("java.lang.Void");

	private final String wrapperClassName;

	private XBasicType(String wrapperClassName) {
		this.wrapperClassName = wrapperClassName;
	}

	public String getWrapperClassName() {
		return wrapperClassName;
	}

	@Override
	public <R> R accept(XTypeVisitor<R> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
