package org.codegeny.semver;

import java.util.function.Function;
import java.util.function.Supplier;

public enum Change {

	MAJOR(Version::nextMajorVersion),
	MINOR(Version::nextMinorVersion),
	PATCH(Version::nextPatchVersion);

	private final Function<Version, Version> next;
	
	private Change(Function<Version, Version> next) {
		this.next = next;
	}
	
	public Change combine(Change that) {
		return that == null || compareTo(that) < 0 ? this : that;
	}
	
	public Version nextVersion(Version version) {
		return this.next.apply(version);
	}
	
	public Change when(boolean condition) {
		return when(condition, () -> PATCH);
	}
	
	public Change when(boolean condition, Supplier<Change> otherwise) {
		return condition ? this : otherwise.get();
	}
}
