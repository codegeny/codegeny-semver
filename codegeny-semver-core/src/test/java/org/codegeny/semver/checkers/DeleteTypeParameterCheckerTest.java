package org.codegeny.semver.checkers;

import java.lang.reflect.GenericDeclaration;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

public class DeleteTypeParameterCheckerTest extends AbstractChangeCheckerTest<GenericDeclaration> {
	
	interface TestType1 {}
	interface TestType2<A> {}
	interface TestType3<A, B> {}
	
	@Parameters(name = NAME)
	public static Collection<?> parameters() {
		return classes(
			patch(TestType1.class, TestType1.class), // no change
			patch(TestType1.class, TestType2.class), // not applicable
			patch(TestType1.class, TestType3.class), // not applicable
			
			major(TestType2.class, TestType1.class), // altered API and not compatible
			patch(TestType2.class, TestType2.class), // no change
			patch(TestType2.class, TestType3.class), // not applicable
			
			major(TestType3.class, TestType1.class), // altered API and not compatible
			major(TestType3.class, TestType2.class), // altered API and not compatible
			patch(TestType3.class, TestType3.class)  // no change
		);
	}
	
	public DeleteTypeParameterCheckerTest() {
		super(GenericDeclarationCheckers.DELETE_TYPE_PARAMETER);
	}
}
