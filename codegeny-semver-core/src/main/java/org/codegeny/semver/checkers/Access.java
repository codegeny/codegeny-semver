package org.codegeny.semver.checkers;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

public enum Access {
	
	PACKAGE(1), PRIVATE(0), PROTECTED(2), PUBLIC(4);
	
	public static Access of(Class<?> klass) {
		return of(klass.getModifiers());
	}
	
	private static Access of(int modifiers) {
		if (Modifier.isPublic(modifiers)) {
			return PUBLIC;
		} else if (Modifier.isProtected(modifiers)) {
			return PROTECTED;
		} else if (Modifier.isPrivate(modifiers)) {
			return PRIVATE;
		} else {
			return PACKAGE;
		}
	}
	
	public static Access of(Member member) {
		return of(member.getModifiers());
	}
	
	private final int order;

	private Access(int order) {
		this.order = order;
	}
	
	public boolean isGreaterThan(Access that) {
		return this.order > that.order;
	}
	
	public boolean isLesserThan(Access that) {
		return this.order < that.order;
	}
}