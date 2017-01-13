package org.codegeny.semver;

import org.junit.Assert;
import org.junit.Test;

public class VersionTest {

	@Test
	public void formatShouldAlwaysSucceed() {
		Assert.assertEquals("1.2.3", new Version(1, 2, 3).toString());
	}
	
	@Test
	public void nextVersionShouldAlwaysSucceed() {
		Version version = Version.parseVersion("1.2.3");
		Assert.assertEquals(new Version(1, 2, 4), version.nextPatchVersion());
		Assert.assertEquals(new Version(1, 3, 0), version.nextMinorVersion());
		Assert.assertEquals(new Version(2, 0, 0), version.nextMajorVersion());
	}
	
	@Test(expected = Exception.class)
	public void parseShouldFailOnInvalidPattern() {
		Version.parseVersion("a.b.c");
	}
	
	@Test(expected = Exception.class)
	public void previousVersionFromMajorVersionShouldFail() {
		new Version(2, 0, 0).previousVersion();		
	}
	
	@Test(expected = Exception.class)
	public void previousVersionFromMinorVersionShouldFail() {
		new Version(1, 2, 0).previousVersion();		
	}
	
	@Test
	public void previousVersionFromPatchVersionShouldSucceed() {
		Assert.assertEquals(new Version(1, 2, 3), new Version(1, 2, 4).previousVersion());		
	}
}
