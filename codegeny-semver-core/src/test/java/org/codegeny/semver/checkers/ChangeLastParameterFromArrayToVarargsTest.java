package org.codegeny.semver.checkers;

import java.lang.reflect.Executable;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

public class ChangeLastParameterFromArrayToVarargsTest extends AbstractChangeCheckerTest<Executable> {
	
	interface TestType1 {
		
		void testMethod(int a, int... b);
	}
	
	interface TestType2 {
		
		void testMethod(int a, int[] b);
	}
	
	@Parameters(name = NAME)
	public static Collection<?> parameters() {
		return methods(
			patch(TestType1.class, TestType1.class), // no change
			patch(TestType1.class, TestType2.class), // not applicable
			
			minor(TestType2.class, TestType1.class), // not applicable
			patch(TestType2.class, TestType2.class)  // no change
		);
	}
	
	public ChangeLastParameterFromArrayToVarargsTest() {
		super(new ChangeLastParameterFromArrayToVarargsChecker());
	}
}
