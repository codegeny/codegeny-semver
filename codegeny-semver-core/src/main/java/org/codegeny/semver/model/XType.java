package org.codegeny.semver.model;

public interface XType {
	
	<R> R accept(XTypeVisitor<R> visitor);
	
	default XType toArray() {
		return new XArrayType(this);
	}
}
