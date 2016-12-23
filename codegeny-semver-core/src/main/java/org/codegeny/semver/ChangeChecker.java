package org.codegeny.semver;

public interface ChangeChecker<T> {
	
	Change check(T previous, T current);
}
