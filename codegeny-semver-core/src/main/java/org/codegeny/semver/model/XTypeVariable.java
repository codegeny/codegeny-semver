package org.codegeny.semver.model;

import java.util.Objects;

public class XTypeVariable implements XType, XNamed {
	
	private final String name;
	
	XTypeVariable(String name) {
		this.name = Objects.requireNonNull(name, "XTypeVariable.name cannot be null");
	}

	public String getName() {
		return name;
	}
	
	@Override
	public <R> R accept(XTypeVisitor<R> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
	
	@Override
	public boolean equals(Object that) {
		return super.equals(that) || that instanceof XTypeVariable && name.equals(((XTypeVariable) that).name);
	}
}
