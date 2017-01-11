package org.codegeny.semver.checkers;

import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

public class ChangePublicToNonPublicCheckerTest extends AbstractChangeCheckerTest<Class<?>> {
	
	public interface TestType1 {}
	private interface TestType2 {}
	
	@Parameters(name = NAME)
	public static Collection<?> parameters() {
		return classes(
			patch(TestType1.class, TestType1.class), // no change
			major(TestType1.class, TestType2.class), // incompatible
			
			patch(TestType2.class, TestType1.class), // not applicable
			patch(TestType2.class, TestType2.class)  // no change
		);
	}
	
	public ChangePublicToNonPublicCheckerTest() {
		super(ClassCheckers.TYPE_CHANGE_PUBLIC_TO_NON_PUBLIC);
	}
}
