package org.codegeny.semver.checkers;

import static org.codegeny.semver.Change.PATCH;

import java.lang.reflect.Constructor;

import org.codegeny.semver.Change;
import org.codegeny.semver.Checker;

public enum ConstructorCheckers implements Checker<Constructor<?>> {

	DUMMY {

		@Override
		public Change check(Constructor<?> previous, Constructor<?> current) {
			return PATCH;
		}
	}
}
