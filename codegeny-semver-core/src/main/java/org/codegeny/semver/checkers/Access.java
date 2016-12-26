package org.codegeny.semver.checkers;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Comparator;

public enum Access {
	
	DEFAULT(1), PRIVATE(0), PROTECTED(2), PUBLIC(4);
	
	public static final Comparator<Access> COMPARATOR = Comparator.comparing(Access::getOrder);
	
	public static Access from(int modifiers) {
		if (Modifier.isPublic(modifiers)) {
			return PUBLIC;
		} else if (Modifier.isProtected(modifiers)) {
			return PROTECTED;
		} else if (Modifier.isPrivate(modifiers)) {
			return PRIVATE;
		} else {
			return DEFAULT;
		}
	}
	
	public static Access from(Member member) {
		return from(member.getModifiers());
	}
	
	private final int order;

	private Access(int order) {
		this.order = order;
	}
	
	public int getOrder() {
		return order;
	}
}