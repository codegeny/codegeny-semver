package org.codegeny.semver.checkers;

import static org.codegeny.semver.checkers.FieldCheckers.CHANGE_TYPE;

import java.lang.reflect.Field;
import java.util.Collection;

import org.junit.Ignore;
import org.junit.runners.Parameterized.Parameters;

@Ignore
public class FieldCheckersTest extends AbstractCheckersTest<Field, FieldCheckers> {
	
	static class TestType1 {
		
		int age;
	}
	
	static class TestType2 {
		
		long age;
	}
	
	@Parameters(name = "{0}")
	public static Collection<?> parameters() {
		return fields(
			data(null, null),
			data(null, TestType1.class),
			data(TestType1.class, null),
			data(TestType1.class, TestType1.class), 
			data(TestType1.class, TestType2.class, CHANGE_TYPE)
		);
	}
	
	public FieldCheckersTest() {
		super(FieldCheckers.class);
	}
}
