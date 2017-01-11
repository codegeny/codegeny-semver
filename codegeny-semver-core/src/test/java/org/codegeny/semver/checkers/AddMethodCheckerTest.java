package org.codegeny.semver.checkers;

import java.lang.reflect.Method;
import java.util.Collection;

import org.junit.Ignore;
import org.junit.runners.Parameterized.Parameters;

@Ignore
public class AddMethodCheckerTest extends AbstractChangeCheckerTest<Method> {
	
	interface TestType1 {
		
	}
	
	interface TestType2 {
		
		void testMethod(int a, int[] b);
	}
	
	interface TestType3 {
		
		default void testMethod(int a, int[] b) {}
	}
	
	public static class TestType4 {
		
		 public final void testMethod(int a, int[] b) {}
	}
	
	static abstract class TestType5 {
		
		 abstract void testMethod(int a, int[] b);
	}
	
	@Parameters(name = NAME)
	public static Collection<?> parameters() {
		return methods(
			patch(TestType1.class, TestType1.class), // not applicable
			major(TestType1.class, TestType2.class), 
			minor(TestType1.class, TestType3.class), 
			minor(TestType1.class, TestType4.class), 
			major(TestType1.class, TestType5.class)  
		);
	}
	
	public AddMethodCheckerTest() {
		super(MethodCheckers.ADD_METHOD);
	}
}