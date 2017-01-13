package org.codegeny.semver.checkers;

import static org.codegeny.semver.Change.MAJOR;
import static org.codegeny.semver.checkers.Checkers.notNull;

import java.lang.reflect.Field;

import org.codegeny.semver.Change;
import org.codegeny.semver.Checker;
import org.codegeny.semver.Metadata;

public enum FieldCheckers implements Checker<Field> {

	CHANGE_TYPE {
		
		@Override
		public Change check(Field previous, Field current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && !previous.getType().getName().equals(current.getType().getName()));
		}
	}
}
