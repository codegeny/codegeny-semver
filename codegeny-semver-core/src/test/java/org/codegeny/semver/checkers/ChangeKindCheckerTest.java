package org.codegeny.semver.checkers;

import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

public class ChangeKindCheckerTest extends AbstractChangeCheckerTest<Class<?>> {
	
	interface TestType1 {}
	static class TestType2 {}
	@interface TestType3 {}
	enum TestType4 {}
	
	@Parameters(name = NAME)
	public static Collection<?> parameters() {
		return classes(
			patch(TestType1.class, TestType1.class), // no change
			major(TestType1.class, TestType2.class), // altered API and not compatible
			major(TestType1.class, TestType3.class), // altered API and not compatible
			major(TestType1.class, TestType4.class), // altered API and not compatible
			
			major(TestType2.class, TestType1.class), // altered API and not compatible
			patch(TestType2.class, TestType2.class), // no change
			major(TestType2.class, TestType3.class), // altered API and not compatible
			major(TestType2.class, TestType4.class), // altered API and not compatible
			
			major(TestType3.class, TestType1.class), // altered API and not compatible
			major(TestType3.class, TestType2.class), // altered API and not compatible
			patch(TestType3.class, TestType3.class), // no change
			major(TestType3.class, TestType4.class), // altered API and not compatible
			
			major(TestType4.class, TestType1.class), // altered API and not compatible
			major(TestType4.class, TestType2.class), // altered API and not compatible
			major(TestType4.class, TestType3.class), // altered API and not compatible
			patch(TestType4.class, TestType4.class)  //no change
		);
	}
	
	public ChangeKindCheckerTest() {
		super(ClassCheckers.TYPE_CHANGE_KIND);
	}
}
