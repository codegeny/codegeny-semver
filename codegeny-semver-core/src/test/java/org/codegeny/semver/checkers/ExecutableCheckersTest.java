package org.codegeny.semver.checkers;

import static org.codegeny.semver.checkers.ExecutableCheckers.ADD_CHECKED_EXCEPTION;
import static org.codegeny.semver.checkers.ExecutableCheckers.CHANGE_LAST_PARAMETER_FROM_ARRAY_TO_VARARGS;
import static org.codegeny.semver.checkers.ExecutableCheckers.CHANGE_LAST_PARAMETER_FROM_VARARGS_TO_ARRAY;
import static org.codegeny.semver.checkers.ExecutableCheckers.REMOVE_CHECKED_EXCEPTION;

import java.lang.reflect.Executable;
import java.util.Collection;

import org.codegeny.semver.checkers.ExecutableCheckers;
import org.junit.runners.Parameterized.Parameters;

public class ExecutableCheckersTest extends AbstractCheckersTest<Executable, ExecutableCheckers> {
	
	interface TestType1 {
		
		int method();
	}
	
	interface TestType2 {
		
		int method() throws Exception;
	}
	
	interface TestType3 {
		
		int method() throws RuntimeException;
	}
	
	interface TestType4 {
		
		int method() throws Throwable;
	}
	
	interface TestType5 {
		
		int method() throws Error;
	}
	
	interface TestType6 {
		
		int method(int... things);
	}
	
	interface TestType7 {
		
		int method(int[] things);
	}

	@Parameters(name = "{0}")
	public static Collection<?> parameters() {
		return methods(
			data(null, null),
			data(null, TestType1.class),
			data(TestType1.class, null),
			data(TestType1.class, TestType1.class),
			data(TestType1.class, TestType2.class, ADD_CHECKED_EXCEPTION),
			data(TestType1.class, TestType3.class),
			data(TestType1.class, TestType4.class, ADD_CHECKED_EXCEPTION),
			data(TestType1.class, TestType5.class),
			data(TestType2.class, TestType1.class, REMOVE_CHECKED_EXCEPTION),
			data(TestType3.class, TestType1.class),
			data(TestType4.class, TestType1.class, REMOVE_CHECKED_EXCEPTION),
			data(TestType5.class, TestType1.class),
			data(TestType6.class, TestType7.class, CHANGE_LAST_PARAMETER_FROM_VARARGS_TO_ARRAY),
			data(TestType7.class, TestType6.class, CHANGE_LAST_PARAMETER_FROM_ARRAY_TO_VARARGS)
		);
	}
	
	public ExecutableCheckersTest() {
		super(ExecutableCheckers.class);
	}
}
