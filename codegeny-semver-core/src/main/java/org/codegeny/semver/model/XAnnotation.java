package org.codegeny.semver.model;

import static java.util.stream.Collectors.joining;

import java.util.Map;
import java.util.TreeMap;

public class XAnnotation {
	
	private final XClass annotationType;
	private final Map<String, XAttribute> attributes = new TreeMap<>();
		
	public XAnnotation(XClass annotationType, Map<String, XAttribute> attributes) {
		this.annotationType = annotationType;
		this.attributes.putAll(attributes);
	}

	public Map<String, XAttribute> getAttributes() {
		return this.attributes;
	}
	
	public XClass getAnnotationType() {
		return annotationType;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("@").append(annotationType.getName());
		if (!attributes.isEmpty()) {
			builder.append(attributes.entrySet().stream().map(e -> String.format("%s = %s", e.getKey(), e.getValue())).collect(joining(", ", "(", ")")));
		}
		return builder.toString();
	}
}
