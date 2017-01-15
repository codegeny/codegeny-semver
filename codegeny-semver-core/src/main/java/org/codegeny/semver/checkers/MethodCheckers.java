package org.codegeny.semver.checkers;

import static org.codegeny.semver.Change.MAJOR;
import static org.codegeny.semver.Change.MINOR;
import static org.codegeny.semver.checkers.Checkers.compare;
import static org.codegeny.semver.checkers.Checkers.fromAnnotations;
import static org.codegeny.semver.checkers.Checkers.isAbstract;
import static org.codegeny.semver.checkers.Checkers.isFinal;
import static org.codegeny.semver.checkers.Checkers.isStatic;
import static org.codegeny.semver.checkers.Checkers.notNull;

import java.lang.reflect.Method;

import org.codegeny.semver.Change;
import org.codegeny.semver.Checker;
import org.codegeny.semver.Metadata;

public enum MethodCheckers implements Checker<Method> {
	
	ADD_ANNOTATION_ELEMENT_WITH_DEFAULT_VALUE {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MINOR.when(previous == null && current != null && current.getDeclaringClass().isAnnotation() && current.getDefaultValue() != null);
		}
	},
	ADD_ANNOTATION_ELEMENT_WITHOUT_DEFAULT_VALUE {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MAJOR.when(previous == null && current != null && current.getDeclaringClass().isAnnotation() && current.getDefaultValue() == null);
		}
	},
	ADD_DEFAULT_CLAUSE {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MINOR.when(fromAnnotations(previous, current) && previous.getDefaultValue() == null && current.getDefaultValue() != null);
		}
	},
	ADD_DEFAULT_METHOD_IMPLEMENTABLE_BY_CLIENT {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MAJOR.when(previous == null && current != null && current.isDefault() && metadata.isImplementedByClient(current.getDeclaringClass()));
		}
	},
	ADD_DEFAULT_METHOD_NOT_IMPLEMENTABLE_BY_CLIENT {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MINOR.when(previous == null && current != null && current.isDefault() && !metadata.isImplementedByClient(current.getDeclaringClass()));
		}
	},
	ADD_NON_DEFAULT_METHOD_IMPLEMENTABLE_BY_CLIENT {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MAJOR.when(previous == null && current != null && !current.getDeclaringClass().isAnnotation() && !current.isDefault() && !isStatic(current) && metadata.isImplementedByClient(current));
		}
	},
	ADD_NON_DEFAULT_METHOD_NOT_IMPLEMENTABLE_BY_CLIENT {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MINOR.when(previous == null && current != null && !current.getDeclaringClass().isAnnotation() && !current.isDefault() && !isStatic(current) && !metadata.isImplementedByClient(current));
		}
	},
	ADD_STATIC_METHOD {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MINOR.when(previous == null && current != null && isStatic(current));
		}		
	},
	CHANGE_ABSTRACT_TO_NON_ABSTRACT {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MINOR.when(notNull(previous, current) && isAbstract(previous) && !current.isDefault() && !isAbstract(current) && !isStatic(current));
		}
	},
	CHANGE_ABSTRACT_TO_DEFAULT {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MINOR.when(notNull(previous, current) && isAbstract(previous) && current.isDefault());
		}
	},
	CHANGE_DEFAULT_TO_ABSTRACT {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && previous.isDefault() && isAbstract(current));
		}
	},
	CHANGE_DEFAULT_CLAUSE {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			// TODO Objects.equals() is not sufficient (what about Class attributes?)
			return MINOR.when(fromAnnotations(previous, current) && previous.getReturnType().getName().equals(current.getReturnType().getName()) && notNull(previous.getDefaultValue(), current.getDefaultValue()) && !compare(previous.getDefaultValue(), current.getDefaultValue()));
		}
	},
	CHANGE_FINAL_TO_NON_FINAL {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MINOR.when(notNull(previous, current) && isFinal(previous) && !isFinal(current));
		}
	},
	CHANGE_NON_ABSTRACT_TO_ABSTRACT {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && !isAbstract(previous) && !previous.isDefault() && isAbstract(current));
		}
	},
	CHANGE_NON_FINAL_TO_FINAL {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			// TODO implementable by client
			return MAJOR.when(notNull(previous, current) && !isFinal(previous) && isFinal(current));
		}
	},
	CHANGE_RESULT_TYPE {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && !previous.getReturnType().getName().equals(current.getReturnType().getName()));
		}
	},
	REMOVE_DEFAULT_CLAUSE {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MAJOR.when(fromAnnotations(previous, current) && previous.getDefaultValue() != null && current.getDefaultValue() == null);
		}
	}
}
