package org.codegeny.semver.checkers;

import static org.codegeny.semver.checkers.ConstructorCheckers.ADD_CONSTRUCTOR_IF_NO_CONSTRUCTORS_EXISTS;
import static org.codegeny.semver.checkers.ConstructorCheckers.ADD_CONSTRUCTOR_IF_OTHER_CONSTRUCTORS_EXISTS;

import java.lang.reflect.Constructor;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

public class ConstructorCheckersTest extends AbstractCheckersTest<Constructor<?>, ConstructorCheckers> {
		
	static class TestType2 {
		
		TestType2(int p) {}
	}
	
	static class TestType3 {
		
		TestType3(int p) {}
		
		TestType3(int p, int o) {}
	}
	
	@Parameters(name = "{0}")
	public static Collection<?> parameters() {
		return constructors(
			data(null, null),
			data(TestType2.class, TestType3.class),
			data(TestType2.class, null), 
			data(TestType3.class, null),
			data(null, TestType2.class, ADD_CONSTRUCTOR_IF_NO_CONSTRUCTORS_EXISTS), 
			data(null, TestType3.class, ADD_CONSTRUCTOR_IF_OTHER_CONSTRUCTORS_EXISTS)
		);
	}
	
	public ConstructorCheckersTest() {
		super(ConstructorCheckers.class);
	}
}
