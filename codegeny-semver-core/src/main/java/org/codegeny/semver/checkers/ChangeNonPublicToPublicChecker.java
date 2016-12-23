package org.codegeny.semver.checkers;

import java.lang.reflect.Modifier;

import org.codegeny.semver.Change;
import org.codegeny.semver.ClassChangeChecker;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class ChangeNonPublicToPublicChecker implements ClassChangeChecker {

	@Override
	public Change check(Class<?> previous, Class<?> current) {
		if (previous == null || current == null) {
			return Change.PATCH;
		}
		if (!Modifier.isPublic(previous.getModifiers()) && Modifier.isPublic(current.getModifiers())) {
			return Change.MINOR;
		}
		return Change.PATCH;
	}
}