package org.codegeny.semver.checkers;

public enum Kind {
	
	INTERFACE, ENUM, CLASS, ANNOTATION;
	
	public static Kind of(Class<?> klass) {
		if (klass.isAnnotation()) {
			return ANNOTATION;
		} else if (klass.isEnum()) {
			return ENUM;
		} else if (klass.isInterface()) {
			return INTERFACE;
		} else {
			return CLASS;
		}
	}
}
