package org.codegeny.semver.checkers;

import static org.codegeny.semver.Change.MAJOR;
import static org.codegeny.semver.Change.MINOR;
import static org.codegeny.semver.checkers.Checkers.isStatic;
import static org.codegeny.semver.checkers.Checkers.notNull;

import java.lang.reflect.Member;

import org.codegeny.semver.Change;
import org.codegeny.semver.Checker;
import org.codegeny.semver.Metadata;

public enum MemberCheckers implements Checker<Member> {
	
	DECREASE_ACCESS {
		
		@Override
		public Change check(Member previous, Member current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && Access.of(current).isLesserThan(Access.of(previous)));
		}
	},
	INCREASE_ACCESS {
		
		@Override
		public Change check(Member previous, Member current, Metadata metadata) {
			return MINOR.when(notNull(previous, current) && Access.of(current).isGreaterThan(Access.of(previous)));
		}
	},
	CHANGE_STATIC_TO_NON_STATIC {
		
		@Override
		public Change check(Member previous, Member current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && isStatic(previous) && !isStatic(current));
		}
	},
	CHANGE_NON_STATIC_TO_STATIC {
		
		@Override
		public Change check(Member previous, Member current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && !isStatic(previous) && isStatic(current));
		}
	},
	DELETE_MEMBER {
		
		@Override
		public Change check(Member previous, Member current, Metadata metadata) {
			return MAJOR.when(previous != null && current == null);
		}
	}
}
