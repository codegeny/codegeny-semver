package org.codegeny.semver.checkers;

import static org.codegeny.semver.Change.MAJOR;
import static org.codegeny.semver.Change.MINOR;

import java.lang.reflect.Constructor;

import org.codegeny.semver.Change;
import org.codegeny.semver.Checker;
import org.codegeny.semver.Metadata;

public enum ConstructorCheckers implements Checker<Constructor<?>> {

	ADD_CONSTRUCTOR_IF_NO_CONSTRUCTORS_EXISTS {

		@Override
		public Change check(Constructor<?> previous, Constructor<?> current, Metadata metadata) {
			return MAJOR.when(previous == null && current != null && current.getDeclaringClass().getDeclaredConstructors().length == 1);
		}
	},
	ADD_CONSTRUCTOR_IF_OTHER_CONSTRUCTORS_EXISTS {

		@Override
		public Change check(Constructor<?> previous, Constructor<?> current, Metadata metadata) {
			return MINOR.when(previous == null && current != null && current.getDeclaringClass().getDeclaredConstructors().length > 1);
		}
	}
}
