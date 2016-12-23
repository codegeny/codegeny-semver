package org.codegeny.semver.checkers;

import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

public class ChangeNonPublicToPublicCheckerTest extends AbstractChangeCheckerTest<Class<?>> {
	
	public interface TestType1 {}
	private interface TestType2 {}
	
	@Parameters(name = NAME)
	public static Collection<?> parameters() {
		return checks(
			patch(TestType1.class, TestType1.class), // no change
			patch(TestType1.class, TestType2.class), // not applicable
			
			minor(TestType2.class, TestType1.class), // augmented api
			patch(TestType2.class, TestType2.class)  // no change
		);
	}
	
	public ChangeNonPublicToPublicCheckerTest() {
		super(new ChangeNonPublicToPublicChecker());
	}
}
