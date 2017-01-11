package org.codegeny.semver.checkers;

import static java.util.stream.Collectors.toList;
import static org.codegeny.semver.Change.MAJOR;
import static org.codegeny.semver.Change.MINOR;
import static org.codegeny.semver.checkers.Access.PUBLIC;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import org.codegeny.semver.Change;
import org.codegeny.semver.Checker;

public enum ClassCheckers implements Checker<Class<?>> {

	ENUM_ADD_CONSTANT {
		
		@Override
		public Change check(Class<?> previous, Class<?> current) {
			return MINOR.when(enumConstants(previous, current, (p, c) -> !p.containsAll(c)));
		}
	},
	ENUM_DELETE_CONSTANT {
		
		@Override
		public Change check(Class<?> previous, Class<?> current) {
			return MAJOR.when(enumConstants(previous, current, (p, c) -> !c.containsAll(p)));
		}
	},
	ENUM_REORDER_CONSTANTS {
		
		@Override
		public Change check(Class<?> previous, Class<?> current) {
			return MINOR.when(enumConstants(previous, current, (p, c) -> p.containsAll(c) && c.containsAll(p) && !c.equals(p)));
		}
	},
	TYPE_CHANGE_KIND {
		
		@Override
		public Change check(Class<?> previous, Class<?> current) {
			return MAJOR.when(notNull(previous, current) && Kind.of(previous) != Kind.of(current));
		}
	},
	TYPE_CHANGE_NON_PUBLIC_TO_PUBLIC {
		
		@Override
		public Change check(Class<?> previous, Class<?> current) {
			return MINOR.when(notNull(previous, current) && Access.of(previous) != PUBLIC && Access.of(current) == PUBLIC);
		}
	},
	TYPE_CHANGE_PUBLIC_TO_NON_PUBLIC {
		
		@Override
		public Change check(Class<?> previous, Class<?> current) {
			return MAJOR.when(notNull(previous, current) && Access.of(previous) == PUBLIC && Access.of(current) != PUBLIC);
		}
	};
	
	boolean enumConstants(Class<?> previous, Class<?> current, BiPredicate<? super List<String>, ? super List<String>> predicate) {
		return notNull(previous, current) && previous.isEnum() && current.isEnum() && predicate.test(enumConstantsOf(previous), enumConstantsOf(current));
	}
	
	List<String> enumConstantsOf(Class<?> klass) {
		return Stream.of(klass.asSubclass(Enum.class).getEnumConstants()).map(Enum::name).collect(toList());
	}
	
	boolean notNull(Object previous, Object current) {
		return previous != null && current != null;
	}
}
