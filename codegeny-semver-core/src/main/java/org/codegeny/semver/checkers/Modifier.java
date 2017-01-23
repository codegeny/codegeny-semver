package org.codegeny.semver.checkers;

import java.lang.reflect.Member;
import java.util.function.IntPredicate;

// It would be nice to have a Modifiable interface that would be shared between Class and Member
public enum Modifier {
	
	ABSTRACT(java.lang.reflect.Modifier::isAbstract), FINAL(java.lang.reflect.Modifier::isFinal), STATIC(java.lang.reflect.Modifier::isStatic);
	
	private final IntPredicate predicate;
	
	private Modifier(IntPredicate predicate) {
		this.predicate = predicate;
	}

	public boolean test(Class<?> klass) {
		return predicate.test(klass.getModifiers());
	}
	
	public boolean test(Member member) {
		return predicate.test(member.getModifiers());
	}
}
