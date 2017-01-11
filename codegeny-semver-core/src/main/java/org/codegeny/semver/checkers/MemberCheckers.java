package org.codegeny.semver.checkers;

import static org.codegeny.semver.Change.MAJOR;
import static org.codegeny.semver.Change.MINOR;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

import org.codegeny.semver.Change;
import org.codegeny.semver.Checker;

public enum MemberCheckers implements Checker<Member> {
	
	DECREASE_ACCESS {
		
		@Override
		public Change check(Member previous, Member current) {
			return MAJOR.when(notNull(previous, current) && Access.of(current).isLesserThan(Access.of(previous)));
		}
	},
	INCREASE_ACCESS {
		
		@Override
		public Change check(Member previous, Member current) {
			return MINOR.when(notNull(previous, current) && Access.of(current).isGreaterThan(Access.of(previous)));
		}
	},
	CHANGE_STATIC_TO_NON_STATIC {
		
		@Override
		public Change check(Member previous, Member current) {
			return MAJOR.when(notNull(previous, current) && isStatic(previous) && !isStatic(current));
		}
	},
	CHANGE_NON_STATIC_TO_STATIC {
		
		@Override
		public Change check(Member previous, Member current) {
			return MAJOR.when(notNull(previous, current) && !isStatic(previous) && isStatic(current));
		}
	};
	
	boolean notNull(Object previous, Object current) {
		return previous != null && current != null;
	}
	
	boolean isStatic(Member member) {
		return Modifier.isStatic(member.getModifiers());
	}
}
