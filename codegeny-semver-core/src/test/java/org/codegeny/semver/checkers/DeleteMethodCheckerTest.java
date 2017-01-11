package org.codegeny.semver.checkers;

import java.lang.reflect.Method;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

public class DeleteMethodCheckerTest extends AbstractChangeCheckerTest<Method> {
	
	interface TestType1 {
		
	}
	
	interface TestType2 {
		
		void testMethod(int a, int[] b);
	}
	
	public static class TestType3 {
		
		public void testMethod(int a, int[] b) {}
	}
	
//	public static class TestType4 {
//		
//		 private void testMethod(int a, int[] b) {}
//	}
	
	@Parameters(name = NAME)
	public static Collection<?> parameters() {
		return methods(
			patch(TestType1.class, TestType1.class), // not applicable
			major(TestType2.class, TestType1.class), // 
			major(TestType3.class, TestType1.class)  //
//			minor(TestType4.class, TestType1.class)  // 
		);
	}
	
	public DeleteMethodCheckerTest() {
		super(MethodCheckers.DELETE_METHOD);
	}
}