package org.codegeny.semver.checkers;

import java.lang.reflect.Modifier;

import org.codegeny.semver.Change;
import org.codegeny.semver.ChangeChecker;

public abstract class AbstractChangeNonPublicToPublicChecker<T> implements ChangeChecker<T> {

	@Override
	public Change check(T previous, T current) {
		if (previous == null || current == null) {
			return Change.PATCH;
		}
		if (!Modifier.isPublic(getModifiers(previous)) && Modifier.isPublic(getModifiers(current))) {
			return Change.MINOR;
		}
		return Change.PATCH;
	}
	
	protected abstract int getModifiers(T target);
}