package org.codegeny.semver.checkers;

import static org.codegeny.semver.Change.MAJOR;
import static org.codegeny.semver.Change.MINOR;
import static org.codegeny.semver.Change.PATCH;

import java.lang.reflect.Method;
import java.util.Objects;

import org.codegeny.semver.Change;
import org.codegeny.semver.Checker;

public enum MethodCheckers implements Checker<Method> {
	
	ADD_ANNOTATION_ELEMENT {
		
		@Override
		public Change check(Method previous, Method current) {
			return notNull(previous, current) && previous.getDeclaringClass().isAnnotation() ? current.getDefaultValue() == null ? MAJOR : MINOR : PATCH;
		}
	},
	ADD_DEFAULT_CLAUSE {
		
		@Override
		public Change check(Method previous, Method current) {
			return MINOR.when(fromAnnotations(previous, current) && previous.getDefaultValue() == null && current.getDefaultValue() != null);
		}
	},
	ADD_METHOD {
		
		@Override
		public Change check(Method previous, Method current) {
//			if (previous != null || current == null) {
//				return PATCH;
//			}
//			if (metadata.isImplementedByClient(current) && !current.isDefault() && !Modifier.isFinal(current.getModifiers())) {
//				return MAJOR;
//			}
//			return MINOR;
			return PATCH;
		}
	},
	CHANGE_DEFAULT_CLAUSE {
		
		@Override
		public Change check(Method previous, Method current) {
			// TODO Objects.equals() is not sufficient (what about Class attributes?)
			return MINOR.when(fromAnnotations(previous, current) && previous.getDefaultValue() != null && current.getDefaultValue() != null && !Objects.equals(previous.getDefaultValue(), current.getDefaultValue()));
		}
	},
	CHANGE_RESULT_TYPE {
		
		@Override
		public Change check(Method previous, Method current) {
			return MAJOR.when(notNull(previous, current) && !previous.getReturnType().getName().equals(current.getReturnType().getName()));
		}
	},
	DELETE_METHOD {
		
		@Override
		public Change check(Method previous, Method current) {
			return MAJOR.when(previous != null && current == null);
		}
	},
	REMOVE_ANNOTATION_ELEMENT {
		
		@Override
		public Change check(Method previous, Method current) {
			return MAJOR.when(notNull(previous, current) && previous.getDeclaringClass().isAnnotation());
		}
	},
	REMOVE_DEFAULT_CLAUSE {
		
		@Override
		public Change check(Method previous, Method current) {
			return MAJOR.when(fromAnnotations(previous, current) && previous.getDefaultValue() != null && current.getDefaultValue() == null);
		}
	};
	
	boolean fromAnnotations(Method previous, Method current) {
		return notNull(previous, current) && previous.getDeclaringClass().isAnnotation() && current.getDeclaringClass().isAnnotation();
	}
	
	boolean notNull(Object previous, Object current) {
		return previous != null && current != null;
	}
}
