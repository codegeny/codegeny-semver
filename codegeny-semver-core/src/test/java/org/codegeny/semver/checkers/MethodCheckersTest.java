package org.codegeny.semver.checkers;

import static org.codegeny.semver.checkers.MethodCheckers.*;

import java.lang.reflect.Method;
import java.util.Collection;

import org.codegeny.semver.checkers.MethodCheckers;
import org.junit.runners.Parameterized.Parameters;

public class MethodCheckersTest extends AbstractCheckersTest<Method, MethodCheckers> {
	
	interface TestType1 {
		
		int age();
	}
	
	interface TestType2 {
		
		default int age() {
			return 0;
		}
	}
	
	interface TestType3 {
		
		static int age() {
			return 0;
		}
	}
	
	interface TestType4 {
		
		long age();
	}
	
	@interface TestType5 {
		
		Class<?> attr();
	}
	
	@interface TestType6 {
		
		Class<?> attr() default Integer.class;
	}
	
	@interface TestType7 {
		
		Class<?> attr() default Long.class;
	}

	@Parameters(name = "{0}")
	public static Collection<?> parameters() {
		return methods(
				
			data(null, null),
			data(null, TestType1.class, ADD_NON_DEFAULT_METHOD_IMPLEMENTABLE_BY_CLIENT),
			data(TestType1.class, null),
			
			data(TestType1.class, TestType1.class),
			
			data(TestType1.class, TestType2.class, CHANGE_ABSTRACT_TO_NON_ABSTRACT),
			data(TestType2.class, TestType1.class, CHANGE_NON_ABSTRACT_TO_ABSTRACT),
			
			data(null, TestType3.class, ADD_STATIC_METHOD), 
			data(TestType1.class, TestType4.class, CHANGE_RESULT_TYPE),
			
			data(null, TestType5.class, ADD_ANNOTATION_ELEMENT_WITHOUT_DEFAULT_VALUE),
			data(null, TestType6.class, ADD_ANNOTATION_ELEMENT_WITH_DEFAULT_VALUE),
			
			data(TestType5.class, TestType6.class, ADD_DEFAULT_CLAUSE),
			data(TestType6.class, TestType5.class, REMOVE_DEFAULT_CLAUSE),
			data(TestType6.class, TestType7.class, CHANGE_DEFAULT_CLAUSE)
		);
	}
	
	public MethodCheckersTest() {
		super(MethodCheckers.class);
	}
}
