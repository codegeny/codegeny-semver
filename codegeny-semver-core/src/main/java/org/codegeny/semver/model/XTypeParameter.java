package org.codegeny.semver.model;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class XTypeParameter implements XBounded, XNamed {
	
	private final String name;
	private XType classBound;
	private final Set<XType> interfaceBounds = new LinkedHashSet<>();
	
	XTypeParameter(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public Set<XType> getInterfaceBounds() {
		return interfaceBounds;
	}
	
	@Override
	public XType getClassBound() {
		return this.classBound;
	}
	
	void addInterfaceBound(XType bound) {
		interfaceBounds.add(bound);
	}
	
	void setClassBound(XType bound) {
		classBound = bound;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(name);
		List<XType> bounds = new ArrayList<>();
		if (classBound != null) {
			bounds.add(classBound);
		}
		bounds.addAll(interfaceBounds);
		if (!bounds.isEmpty()) {
			builder.append(bounds.stream().map(XType::toString).collect(joining(", ", " extends ", "")));
		}
		return builder.toString();
	}
}
