package org.codegeny.semver.checkers;

import org.codegeny.semver.Change;
import org.codegeny.semver.ClassChangeChecker;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class AddTypeParameterChecker implements ClassChangeChecker {

	@Override
	public Change check(Class<?> previous, Class<?> current) {
		if (previous == null || current == null) {
			return Change.PATCH;
		}
		if (current.getTypeParameters().length > previous.getTypeParameters().length) {
			if (previous.getTypeParameters().length == 0) {
				return Change.MINOR;
			} else {
				return Change.MAJOR;
			}
		}
		return Change.PATCH;
	}
}
