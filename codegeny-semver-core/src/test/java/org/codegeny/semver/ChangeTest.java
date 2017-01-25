package org.codegeny.semver;

import static org.codegeny.semver.Change.MAJOR;
import static org.codegeny.semver.Change.MINOR;
import static org.codegeny.semver.Change.PATCH;

import org.junit.Assert;
import org.junit.Test;

public class ChangeTest {
	
	@Test
	public void test() {
		Version version = new Version(1, 2, 3);
		Assert.assertEquals(new Version(1, 2, 4), PATCH.nextVersion(version));
		Assert.assertEquals(new Version(1, 3, 0), MINOR.nextVersion(version));
		Assert.assertEquals(new Version(2, 0, 0), MAJOR.nextVersion(version));
	}
}
