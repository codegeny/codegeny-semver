package org.codegeny.semver.checkers;

import static org.codegeny.semver.Change.MAJOR;
import static org.codegeny.semver.Change.MINOR;
import static org.codegeny.semver.checkers.Checkers.compareTypes;
import static org.codegeny.semver.checkers.Checkers.getCheckedExceptionClassNames;
import static org.codegeny.semver.checkers.Checkers.notNull;

import java.lang.reflect.Executable;

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
//	CHANGE_GENERIC_EXCEPTION_TYPE {
//			
//		// TODO needed for <E extends Exception> m() throws E 
//		@Override
//		public Change check(Executable previous, Executable current, Metadata metadata) {
//			return PATCH;
//			//return MAJOR.when(notNull(previous, current) && !compareTypes(previous.getGenericExceptionTypes(), current.getGenericExceptionTypes()));
//		}
//	},
	CHANGE_GENERIC_PARAMETER_TYPE {
		
		// TODO reconsider severity 
		@Override
		public Change check(Executable previous, Executable current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && !compareTypes(previous.getGenericParameterTypes(), current.getGenericParameterTypes()));
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
	}	
}
