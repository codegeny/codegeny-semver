package org.codegeny.semver.checkers;

import static org.codegeny.semver.Change.MAJOR;
import static org.codegeny.semver.Change.MINOR;
import static org.codegeny.semver.checkers.Checkers.enumConstants;
import static org.codegeny.semver.checkers.Checkers.isAbstract;
import static org.codegeny.semver.checkers.Checkers.isFinal;
import static org.codegeny.semver.checkers.Checkers.isStatic;
import static org.codegeny.semver.checkers.Checkers.notNull;
import static org.codegeny.semver.checkers.Checkers.sameKind;

import org.codegeny.semver.Change;
import org.codegeny.semver.Checker;
import org.codegeny.semver.Metadata;

public enum ClassCheckers implements Checker<Class<?>> {
	
	ENUM_ADD_CONSTANT {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MINOR.when(enumConstants(previous, current, (p, c) -> !p.containsAll(c)));
		}
	},
	ENUM_DELETE_CONSTANT {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MAJOR.when(enumConstants(previous, current, (p, c) -> !c.containsAll(p)));
		}
	},
	ENUM_REORDER_CONSTANTS {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MINOR.when(enumConstants(previous, current, (p, c) -> p.containsAll(c) && c.containsAll(p) && !c.equals(p)));
		}
	},
	TYPE_CHANGE_KIND {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && Kind.of(previous) != Kind.of(current));
		}
	},
	DECREASE_ACCESS {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && Access.of(current).isLesserThan(Access.of(previous)));
		}
	},
	INCREASE_ACCESS {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MINOR.when(notNull(previous, current) && Access.of(current).isGreaterThan(Access.of(previous)));
		}
	},
	TYPE_CHANGE_NON_ABSTRACT_TO_ABSTRACT {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MAJOR.when(sameKind(previous, current) && !isAbstract(previous) && isAbstract(current));
		}
	},
	TYPE_CHANGE_ABSTRACT_TO_NON_ABSTRACT {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MINOR.when(sameKind(previous, current) && isAbstract(previous) && !isAbstract(current));
		}
	},
	TYPE_CHANGE_NON_FINAL_TO_FINAL {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			// TODO implementable by client
			return MAJOR.when(sameKind(previous, current) && !isFinal(previous) && isFinal(current));
		}
	},
	TYPE_CHANGE_FINAL_TO_NON_FINAL {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MINOR.when(sameKind(previous, current) && isFinal(previous) && !isFinal(current));
		}
	},
	CHANGE_STATIC_TO_NON_STATIC {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MAJOR.when(sameKind(previous, current) && isStatic(previous) && !isStatic(current));
		}
	},
	CHANGE_NON_STATIC_TO_STATIC {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MAJOR.when(sameKind(previous, current) && !isStatic(previous) && isStatic(current));
		}
	},
	DELETE_TYPE {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MAJOR.when(previous != null && current == null);
		}
	},
	ADD_TYPE {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MINOR.when(previous == null && current != null);
		}
	},
}
