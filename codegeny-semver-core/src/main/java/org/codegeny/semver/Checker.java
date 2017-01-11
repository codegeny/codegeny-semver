package org.codegeny.semver;

public interface Checker<T> {
	
	Change check(T previous, T current);
}
