package org.codegeny.semver.checkers;

import static java.util.stream.Collectors.toSet;
import static org.codegeny.semver.Change.MAJOR;
import static org.codegeny.semver.Change.MINOR;
import static org.codegeny.semver.checkers.Checkers.enumConstants;
import static org.codegeny.semver.checkers.Checkers.hierarchy;
import static org.codegeny.semver.checkers.Checkers.isAbstract;
import static org.codegeny.semver.checkers.Checkers.isFinal;
import static org.codegeny.semver.checkers.Checkers.isStatic;
import static org.codegeny.semver.checkers.Checkers.notNull;
import static org.codegeny.semver.checkers.Checkers.sameKind;

import org.codegeny.semver.Change;
import org.codegeny.semver.Checker;
import org.codegeny.semver.Metadata;

public enum ClassCheckers implements Checker<Class<?>> {
	
	ADD_ENUM_CONSTANT {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MINOR.when(enumConstants(previous, current, (p, c) -> !p.containsAll(c)));
		}
	},
	ADD_TYPE {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MINOR.when(previous == null && current != null);
		}
	},
	CHANGE_ABSTRACT_TO_NON_ABSTRACT {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MINOR.when(sameKind(previous, current) && isAbstract(previous) && !isAbstract(current));
		}
	},
	CHANGE_FINAL_TO_NON_FINAL {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MINOR.when(sameKind(previous, current) && isFinal(previous) && !isFinal(current));
		}
	},
	CHANGE_KIND {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && Kind.of(previous) != Kind.of(current));
		}
	},
	CHANGE_NON_ABSTRACT_TO_ABSTRACT {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MAJOR.when(sameKind(previous, current) && !isAbstract(previous) && isAbstract(current));
		}
	},
	CHANGE_NON_FINAL_TO_FINAL {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			// TODO implementable by client
			return MAJOR.when(sameKind(previous, current) && !isFinal(previous) && isFinal(current));
		}
	},
	CHANGE_NON_STATIC_TO_STATIC {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MAJOR.when(sameKind(previous, current) && !isStatic(previous) && isStatic(current));
		}
	},
	CHANGE_STATIC_TO_NON_STATIC {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MAJOR.when(sameKind(previous, current) && isStatic(previous) && !isStatic(current));
		}
	},
	DECREASE_ACCESS {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && Access.of(current).isLesserThan(Access.of(previous)));
		}
	},
	DELETE_ENUM_CONSTANT {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MAJOR.when(enumConstants(previous, current, (p, c) -> !c.containsAll(p)));
		}
	},
	DELETE_TYPE {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MAJOR.when(previous != null && current == null);
		}
	},
	INCREASE_ACCESS {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MINOR.when(notNull(previous, current) && Access.of(current).isGreaterThan(Access.of(previous)));
		}
	},
	REORDER_ENUM_CONSTANTS {
		
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MINOR.when(enumConstants(previous, current, (p, c) -> p.containsAll(c) && c.containsAll(p) && !c.equals(p)));
		}
	},
	EXPAND_SUPERTYPES_SET {
		
		// TODO generic
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MINOR.when(sameKind(previous, current) && !hierarchy(previous).map(Class::getName).collect(toSet()).containsAll(hierarchy(current).map(Class::getName).collect(toSet())));
		}
	},
	CONTRACT_SUPERTYPES_SET {
		
		// TODO generic
		@Override
		public Change check(Class<?> previous, Class<?> current, Metadata metadata) {
			return MAJOR.when(sameKind(previous, current) && !hierarchy(current).map(Class::getName).collect(toSet()).containsAll(hierarchy(previous).map(Class::getName).collect(toSet())));
		}
	}
}
