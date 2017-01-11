package org.codegeny.semver.checkers;

import static org.codegeny.semver.Change.MAJOR;

import java.lang.reflect.Field;

import org.codegeny.semver.Change;
import org.codegeny.semver.Checker;

public enum FieldCheckers implements Checker<Field> {

	CHANGE_TYPE {
		
		@Override
		public Change check(Field previous, Field current) {
			return MAJOR.when(notNull(previous, current) && !previous.getType().getName().equals(current.getType().getName()));
		}
	};
	
	boolean notNull(Object previous, Object current) {
		return previous != null && current != null;
	}
}
