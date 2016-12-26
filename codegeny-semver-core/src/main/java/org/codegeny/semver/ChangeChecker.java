package org.codegeny.semver;

public interface ChangeChecker<T> {
	
	Change NOT_APPLICABLE = Change.PATCH;
	
	Change check(T previous, T current);
}
