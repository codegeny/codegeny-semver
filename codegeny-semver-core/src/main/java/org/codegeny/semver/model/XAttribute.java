package org.codegeny.semver.model;

public interface XAttribute {
	
	<R> R accept(XAttributeVisitor<R> visitor);
}
