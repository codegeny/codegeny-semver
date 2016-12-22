package org.codegeny.semver.model;

import java.util.LinkedList;
import java.util.List;

public class XPackage implements XNamed, XAnnotable {
	
	private final List<XAnnotation> annotations = new LinkedList<>();
	private final String name;

	public XPackage(String name) {
		this.name = name;
	}
	
	void addAnnotation(XAnnotation annotation) {
		this.annotations.add(annotation);
	}

	@Override
	public List<XAnnotation> getAnnotations() {
		return this.annotations;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return "package ".concat(this.name);
	}
	
	@Override
	public boolean equals(Object that) {
		return super.equals(that) || that instanceof XPackage && name.equals(((XPackage) that).name);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
