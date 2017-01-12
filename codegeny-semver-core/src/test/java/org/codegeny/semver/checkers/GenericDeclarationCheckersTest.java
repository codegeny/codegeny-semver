package org.codegeny.semver.checkers;

import static org.codegeny.semver.checkers.GenericDeclarationCheckers.ADD_TYPE_PARAMETER_WHEN_NO_PARAMETERS_EXIST;
import static org.codegeny.semver.checkers.GenericDeclarationCheckers.ADD_TYPE_PARAMETER_WHEN_OTHER_PARAMETERS_EXIST;
import static org.codegeny.semver.checkers.GenericDeclarationCheckers.CHANGE_TYPE_PARAMETERS_BOUNDS;
import static org.codegeny.semver.checkers.GenericDeclarationCheckers.DELETE_TYPE_PARAMETER;

import java.lang.reflect.GenericDeclaration;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

public class GenericDeclarationCheckersTest extends AbstractCheckersTest<GenericDeclaration, GenericDeclarationCheckers> {
	
	interface TestType1 {
		
		void m();
	}
	
	interface TestType2 {
		
		<T> void m();
	}
	
	interface TestType3 {
		
		<T, E> void m();
	}
	
	interface TestType4 {
		
		<T extends CharSequence> void m();
	}
	
	@Parameters(name = "{0}")
	public static Collection<?> parameters() {
		return methods(
			data(null, null),
			data(null, TestType1.class),
			data(TestType1.class, null),
			data(TestType1.class, TestType1.class),
			data(TestType1.class, TestType2.class, ADD_TYPE_PARAMETER_WHEN_NO_PARAMETERS_EXIST),
			data(TestType2.class, TestType3.class, ADD_TYPE_PARAMETER_WHEN_OTHER_PARAMETERS_EXIST),
			data(TestType2.class, TestType1.class, DELETE_TYPE_PARAMETER),
			data(TestType2.class, TestType4.class, CHANGE_TYPE_PARAMETERS_BOUNDS)
		);
	}
	
	public GenericDeclarationCheckersTest() {
		super(GenericDeclarationCheckers.class);
	}
}