package org.codegeny.semver.checkers;

import static org.codegeny.semver.Change.PATCH;

import java.lang.reflect.Field;

import org.codegeny.semver.Change;
import org.codegeny.semver.Checker;

public enum FieldCheckers implements Checker<Field> {

	DUMMY {

		@Override
		public Change check(Field previous, Field current) {
			return PATCH;
		}
	}
}
