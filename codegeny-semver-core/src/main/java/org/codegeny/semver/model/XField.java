package org.codegeny.semver.model;

import java.util.ArrayList;
import java.util.List;

public class XField implements XAnnotable, XNamed {
	
	private final List<XAnnotation> annotations = new ArrayList<>();
	private final String name;
	private XType type;
	
	XField(String name) {
		this.name = name;
	}
	
	void addAnnotation(XAnnotation annotation) {
		annotations.add(annotation);
	}
	
	@Override
	public List<XAnnotation> getAnnotations() {
		return annotations;
	}

	public String getName() {
		return name;
	}
	
	public XType getType() {
		return type;
	}
	
	void setType(XType type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append(type).append(" ").append(name).toString();
	}
}
