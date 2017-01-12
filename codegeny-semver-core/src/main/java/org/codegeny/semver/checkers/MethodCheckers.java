package org.codegeny.semver.checkers;

import static org.codegeny.semver.Change.MAJOR;
import static org.codegeny.semver.Change.MINOR;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

import org.codegeny.semver.Change;
import org.codegeny.semver.Checker;
import org.codegeny.semver.Metadata;

public enum MethodCheckers implements Checker<Method> {
	
	ADD_STATIC_METHOD {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MINOR.when(previous == null && current != null && isStatic(current));
		}		
	},
	ADD_ANNOTATION_ELEMENT_WITH_DEFAULT_VALUE {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MINOR.when(notNull(previous, current) && previous.getDeclaringClass().isAnnotation() && current.getDefaultValue() != null);
		}
	},
	ADD_ANNOTATION_ELEMENT_WITHOUT_DEFAULT_VALUE {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && previous.getDeclaringClass().isAnnotation() && current.getDefaultValue() == null);
		}
	},
	ADD_DEFAULT_CLAUSE {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MINOR.when(fromAnnotations(previous, current) && previous.getDefaultValue() == null && current.getDefaultValue() != null);
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
	CHANGE_DEFAULT_CLAUSE {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			// TODO Objects.equals() is not sufficient (what about Class attributes?)
			return MINOR.when(fromAnnotations(previous, current) && previous.getDefaultValue() != null && current.getDefaultValue() != null && !Objects.equals(previous.getDefaultValue(), current.getDefaultValue()));
		}
	},
	CHANGE_RESULT_TYPE {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && !previous.getReturnType().getName().equals(current.getReturnType().getName()));
		}
	},
	DELETE_METHOD {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MAJOR.when(previous != null && current == null);
		}
	},
	REMOVE_ANNOTATION_ELEMENT {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && previous.getDeclaringClass().isAnnotation());
		}
	},
	CHANGE_NON_ABSTRACT_TO_ABSTRACT {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && !isAbstract(previous) && isAbstract(current));
		}
	},
	CHANGE_ABSTRACT_TO_NON_ABSTRACT {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MINOR.when(notNull(previous, current) && isAbstract(previous) && !isAbstract(current));
		}
	},
	REMOVE_DEFAULT_CLAUSE {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MAJOR.when(fromAnnotations(previous, current) && previous.getDefaultValue() != null && current.getDefaultValue() == null);
		}
	},
	CHANGE_NON_FINAL_TO_FINAL {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			// TODO implementable by client
			return MAJOR.when(notNull(previous, current) && !isFinal(previous) && isFinal(current));
		}
	},
	CHANGE_FINAL_TO_NON_FINAL {
		
		@Override
		public Change check(Method previous, Method current, Metadata metadata) {
			return MINOR.when(notNull(previous, current) && isFinal(previous) && !isFinal(current));
		}
	};
	
	boolean fromAnnotations(Method previous, Method current) {
		return notNull(previous, current) && previous.getDeclaringClass().isAnnotation() && current.getDeclaringClass().isAnnotation();
	}
	
	boolean notNull(Object previous, Object current) {
		return previous != null && current != null;
	}
	
	boolean isAbstract(Method method) {
		return Modifier.isAbstract(method.getModifiers());
	}
	
	boolean isFinal(Method method) {
		return Modifier.isFinal(method.getModifiers());
	}
	
	boolean isStatic(Method method) {
		return Modifier.isStatic(method.getModifiers());
	}
}
