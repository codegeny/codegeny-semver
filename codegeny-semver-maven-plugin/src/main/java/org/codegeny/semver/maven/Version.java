package org.codegeny.semver.maven;

import java.io.Serializable;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Version implements Comparable<Version>, Serializable {
	
	private static final Comparator<Version> COMPARATOR = Comparator.comparingInt(Version::getMajor).thenComparingInt(Version::getMinor).thenComparingInt(Version::getPatch);
	
	private static final Pattern PATTERN = Pattern.compile("(?<major>\\d+)\\.(?<minor>\\d+)\\.(?<patch>\\d+)(?:.*)");
	
	private static final long serialVersionUID = 1L;
	
	public static Version parseVersion(String version) {
		Matcher matcher = PATTERN.matcher(version);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Version is not conform to major.minor.version pattern");
		}
		int major = Integer.parseInt(matcher.group("major"));
		int minor = Integer.parseInt(matcher.group("minor"));
		int patch = Integer.parseInt(matcher.group("patch"));
		return new Version(major, minor, patch);
	}
	
	private final int major, minor, patch;

	public Version(int major, int minor, int patch) {
		if (major < 0 || minor < 0 || patch < 0) {
			throw new IllegalArgumentException("Version must not contain any negative number");
		}
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}
	
	@Override
	public int compareTo(Version that) {
		return COMPARATOR.compare(this, that);
	}
	
	@Override
	public boolean equals(Object that) {
		return super.equals(that) || that instanceof Version && compareTo((Version) that) == 0;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getPatch() {
		return patch;
	}

	@Override
	public int hashCode() {
		return ((31 + this.major) * 31 + this.minor) * 31 + this.patch;
	}
	
	public Version nextMajorVersion() {
		return new Version(this.major + 1, 0, 0);
	}
	
	public Version nextMinorVersion() {
		return new Version(this.major, this.minor + 1, 0);
	}
	
	public Version nextPatchVersion() {
		return new Version(this.major, this.minor, this.patch + 1);
	}
	
	public Version previousVersion() {
		if (this.patch > 0) {
			return new Version(this.major, this.minor, this.patch - 1);
		}
		if (this.minor > 0) {
			return new Version(this.major, this.minor - 1, 0);
		}
		if (this.major > 0) {
			return new Version(this.major - 1, 0, 0);
		}
		throw new IllegalStateException("No previous version exist for " + this);
	}
	
	@Override
	public String toString() {
		return String.format("%d.%d.%d", this.major, this.minor, this.patch);
	}
}
