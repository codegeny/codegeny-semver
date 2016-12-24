package org.codegeny.semver.checkers;

import java.lang.reflect.GenericDeclaration;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

public class AddTypeParameterCheckerTest extends AbstractChangeCheckerTest<GenericDeclaration> {
	
	interface TestType1 {}
	interface TestType2<A> {}
	interface TestType3<A, B> {}
	
	@Parameters(name = NAME)
	public static Collection<?> parameters() {
		return classes(
			patch(TestType1.class, TestType1.class), // no change
			minor(TestType1.class, TestType2.class), // altered API but still compatible
			minor(TestType1.class, TestType3.class), // altered API but still compatible
			
			patch(TestType2.class, TestType1.class), // not applicable
			patch(TestType2.class, TestType2.class), // no change
			major(TestType2.class, TestType3.class), // altered API and not compatible
			
			patch(TestType3.class, TestType1.class), // not applicable
			patch(TestType3.class, TestType2.class), // not applicable
			patch(TestType3.class, TestType3.class)  // no change
		);
	}
	
	public AddTypeParameterCheckerTest() {
		super(new AddTypeParameterChecker());
	}
}
