package org.codegeny.semver.checkers;

import static java.util.stream.Collectors.toSet;
import static org.codegeny.semver.Change.MAJOR;
import static org.codegeny.semver.Change.MINOR;

import java.lang.reflect.Executable;
import java.util.Set;
import java.util.stream.Stream;

import org.codegeny.semver.Change;
import org.codegeny.semver.Checker;
import org.codegeny.semver.Metadata;

public enum ExecutableCheckers implements Checker<Executable> {
	
	ADD_CHECKED_EXCEPTION {
		
		@Override
		public Change check(Executable previous, Executable current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && !getCheckedExceptionClassNames(previous).containsAll(getCheckedExceptionClassNames(current)));
		}
	},
	CHANGE_LAST_PARAMETER_FROM_ARRAY_TO_VARARGS {

		@Override
		public Change check(Executable previous, Executable current, Metadata metadata) {
			return MINOR.when(notNull(previous, current) && !previous.isVarArgs() && current.isVarArgs());
		}
	},
	CHANGE_LAST_PARAMETER_FROM_VARARGS_TO_ARRAY {
	
		@Override
		public Change check(Executable previous, Executable current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && previous.isVarArgs() && !current.isVarArgs());
		}
	},
	REMOVE_CHECKED_EXCEPTION {
		
		@Override
		public Change check(Executable previous, Executable current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && !getCheckedExceptionClassNames(current).containsAll(getCheckedExceptionClassNames(previous)));
		}
	};
	
	Set<String> getCheckedExceptionClassNames(Executable executable) {
		return Stream.of(executable.getExceptionTypes()).filter(this::isChecked).map(Class::getName).collect(toSet());
	}
	
	boolean isChecked(Class<?> type) {
		return !RuntimeException.class.isAssignableFrom(type) && !Error.class.isAssignableFrom(type);
	}
	
	boolean notNull(Object previous, Object current) {
		return previous != null && current != null;
	}
}
