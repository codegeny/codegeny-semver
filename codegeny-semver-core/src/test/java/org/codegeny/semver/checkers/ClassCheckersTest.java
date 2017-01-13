package org.codegeny.semver.checkers;

import static org.codegeny.semver.checkers.ClassCheckers.*;

import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

public class ClassCheckersTest extends AbstractCheckersTest<Class<?>, ClassCheckers> {
	
	enum TestType1 {
		
		ONE
	}
	
	enum TestType2 {
		
		ONE, TWO
	}
	
	enum TestType3 {
		
		TWO, ONE
	}
	
	interface TestType4 {}
	
	public static class TestType5 {}
	
	@interface TestType6 {}
	
	protected static class TestType7 {}
	
	public abstract static class TestType8 {}
	
	public final static class TestType9 {}
	
	@Parameters(name = "{0}")
	public static Collection<?> parameters() {
		return classes(
			data(null, null),
			data(null, TestType1.class),
			data(TestType1.class, null),
			data(TestType1.class, TestType1.class),
			data(TestType1.class, TestType2.class, ENUM_ADD_CONSTANT), 
			data(TestType2.class, TestType1.class, ENUM_DELETE_CONSTANT),
			data(TestType2.class, TestType3.class, ENUM_REORDER_CONSTANTS),
			
			data(TestType3.class, TestType4.class, TYPE_CHANGE_KIND),
			data(TestType3.class, TestType5.class, TYPE_CHANGE_KIND),
			data(TestType3.class, TestType6.class, TYPE_CHANGE_KIND),
			
			data(TestType4.class, TestType3.class, TYPE_CHANGE_KIND),
			data(TestType4.class, TestType5.class, TYPE_CHANGE_KIND),
			data(TestType4.class, TestType6.class, TYPE_CHANGE_KIND),

			data(TestType5.class, TestType3.class, TYPE_CHANGE_KIND),
			data(TestType5.class, TestType4.class, TYPE_CHANGE_KIND),
			data(TestType5.class, TestType6.class, TYPE_CHANGE_KIND),

			data(TestType6.class, TestType3.class, TYPE_CHANGE_KIND),
			data(TestType6.class, TestType4.class, TYPE_CHANGE_KIND),
			data(TestType6.class, TestType5.class, TYPE_CHANGE_KIND),		

			data(TestType5.class, TestType7.class, TYPE_CHANGE_PUBLIC_TO_NON_PUBLIC),
			data(TestType7.class, TestType5.class, TYPE_CHANGE_NON_PUBLIC_TO_PUBLIC),
			
			data(TestType5.class, TestType8.class, TYPE_CHANGE_NON_ABSTRACT_TO_ABSTRACT),
			data(TestType8.class, TestType5.class, TYPE_CHANGE_ABSTRACT_TO_NON_ABSTRACT),
			
			data(TestType5.class, TestType9.class, TYPE_CHANGE_NON_FINAL_TO_FINAL),
			data(TestType9.class, TestType5.class, TYPE_CHANGE_FINAL_TO_NON_FINAL),
			
			data(TestType8.class, TestType8.class)
		);
	}
	
	public ClassCheckersTest() {
		super(ClassCheckers.class);
	}
}
