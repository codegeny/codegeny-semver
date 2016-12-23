package org.codegeny.semver.checkers;

import org.codegeny.semver.Change;
import org.codegeny.semver.ClassChangeChecker;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class ChangeKindChecker implements ClassChangeChecker {

	@Override
	public Change check(Class<?> previous, Class<?> current) {
		if (previous == null || current == null) {
			return Change.PATCH;
		}
		if (previous.isAnnotation() ^ current.isAnnotation()
				|| previous.isInterface() ^ current.isInterface()
				|| previous.isEnum() ^ current.isEnum()) {
			return Change.MAJOR;
		}
		return Change.PATCH;
	}
}
