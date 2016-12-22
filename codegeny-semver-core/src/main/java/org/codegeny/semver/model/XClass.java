package org.codegeny.semver.model;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class XClass implements XParameterizable, XAnnotable, XBounded, XNamed {
	
	private final List<XAnnotation> annotations = new ArrayList<>();
	private final List<XField> fields = new ArrayList<>();
	private final Set<XClassType> interfaces = new HashSet<>();
	private final XKind kind;
	private final List<XMethod> methods = new ArrayList<>();
	private final String name;
	private XClassType superClass;
	private final List<XTypeParameter> typeParameters = new ArrayList<>();
	private final XPackage pakkage;
	
	XClass(String name, XKind kind, XPackage pakkage) {
		this.name = name;
		this.kind = kind;
		this.pakkage = pakkage;
	}
	
	public XPackage getPackage() {
		return this.pakkage;
	}
	
	void addAnnotation(XAnnotation annotation) {
		annotations.add(annotation);
	}
	
	void addField(XField field) {
		fields.add(field);
	}
	
	void addInterface(XType type) {
		interfaces.add((XClassType) type);
	}
	
	void addMethod(XMethod method) {
		methods.add(method);
	}
	
	void addTypeParameter(XTypeParameter typeParameter) {
		typeParameters.add(typeParameter);
	}
	
	@Override
	public List<XAnnotation> getAnnotations() {
		return annotations;
	}
	
	@Override
	public XClassType getClassBound() {
		return superClass;
	}
	
	public List<XField> getFields() {
		return fields;
	}
	
	@Override
	public Set<XClassType> getInterfaceBounds() {
		return interfaces;
	}
	
	public XKind getKind() {
		return kind;
	}
	
	public List<XMethod> getMethods() {
		return methods;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public List<XTypeParameter> getTypeParameters() {
		return typeParameters;
	}
	
	void setSuperClass(XType type) {
		superClass = (XClassType) type;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (!typeParameters.isEmpty()) {
			builder.append(typeParameters.stream().map(XTypeParameter::toString).collect(joining(", ", "<", "> ")));
		}
		builder.append(kind.name().toLowerCase()).append(" ").append(name);
		if (superClass != null) {
			builder.append(" extends ").append(superClass);
		}
		if (!interfaces.isEmpty()) {
			builder.append(interfaces.stream().map(XClassType::toString).collect(joining(", ", " implements ", "")));
		}
		return builder.toString();
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
	
	@Override
	public boolean equals(Object that) {
		return super.equals(that) || that instanceof XClass && name.equals(((XClass) that).name);
	}
}
