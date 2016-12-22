package org.codegeny.semver.model;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class XClassType implements XType {
	
	private final XClass klass;
	private final List<XTypeArgument> typeArguments = new ArrayList<>();
	
	XClassType(XClass klass) {
		this.klass = Objects.requireNonNull(klass);
	}

	@Override
	public <R> R accept(XTypeVisitor<R> visitor) {
		return visitor.visit(this);
	}
	
	void addTypeArgument(XTypeArgument typeArgument) {
		typeArguments.add(typeArgument);
	}
	
	void addTypeArgument(XWildcard wildcard, XType bound) {
		addTypeArgument(new XTypeArgument(wildcard, bound));
	}
	
	void addTypeArgument() {
		addTypeArgument(new XTypeArgument());
	}
	
	public XClass getType() {
		return klass;
	}
	
	public List<XTypeArgument> getTypeArguments() {
		return typeArguments;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.klass.getName());
		if (!typeArguments.isEmpty()) {
			builder.append(typeArguments.stream().map(XTypeArgument::toString).collect(joining(", ", "<", ">")));
		}
		return builder.toString();
	}
}
