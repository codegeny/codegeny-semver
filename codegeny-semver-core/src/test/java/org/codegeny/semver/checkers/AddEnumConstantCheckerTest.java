package org.codegeny.semver.checkers;

import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

public class AddEnumConstantCheckerTest extends AbstractChangeCheckerTest<Class<?>> {
	
	enum TestType1 {
		
		ONE
	}
	
	enum TestType2 {
		
		ONE, TWO
	}
	
	@Parameters(name = NAME)
	public static Collection<?> parameters() {
		return classes(
			patch(TestType1.class, TestType1.class), // no change
			minor(TestType1.class, TestType2.class), // altered API but still compatible
			
			patch(TestType2.class, TestType1.class), // not applicable
			patch(TestType2.class, TestType2.class)  // no change
		);
	}
	
	public AddEnumConstantCheckerTest() {
		super(new AddEnumConstantChecker());
	}
}
