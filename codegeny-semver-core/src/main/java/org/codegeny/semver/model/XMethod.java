package org.codegeny.semver.model;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;

public class XMethod implements XParameterizable, XAnnotable, XNamed {
	
	private final List<XAnnotation> annotations = new ArrayList<>();
	private final List<XType> exceptionTypes = new ArrayList<>();
	private final String name;
	private final List<XType> parameterTypes = new ArrayList<>();
	private XType returnType;
	private final List<XTypeParameter> typeParameters = new ArrayList<>();
	
	XMethod(String name) {
		this.name = name;
	}
	
	void addAnnotation(XAnnotation annotation) {
		annotations.add(annotation);
	}
	
	void addExceptionType(XType exceptionType) {
		exceptionTypes.add(exceptionType);
	}
	
	void addParameterType(XType parameterType) {
		parameterTypes.add(parameterType);
	}
	
	void addTypeParameter(XTypeParameter typeParameter) {
		typeParameters.add(typeParameter);
	}
	
	@Override
	public List<XAnnotation> getAnnotations() {
		return annotations;
	}
	
	public List<XType> getExceptionTypes() {
		return exceptionTypes;
	}

	public String getName() {
		return name;
	}
	
	public List<XType> getParameterTypes() {
		return parameterTypes;
	}
	
	public XType getReturnType() {
		return returnType;
	}
	
	public List<XTypeParameter> getTypeParameters() {
		return typeParameters;
	}
	
	void setReturnType(XType returnType) {
		this.returnType = returnType;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (!typeParameters.isEmpty()) {
			builder.append(typeParameters.stream().map(XTypeParameter::toString).collect(joining(", ", "<", "> ")));
		}
		builder.append(returnType).append(" ").append(name).append(parameterTypes.stream().map(XType::toString).collect(joining(", ", "(", ")")));
		if (!exceptionTypes.isEmpty()) {
			builder.append(exceptionTypes.stream().map(XType::toString).collect(joining(", ", " throws ", "")));
		}
		return builder.toString();
	}
}
