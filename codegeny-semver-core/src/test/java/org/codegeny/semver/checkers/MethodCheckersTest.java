package org.codegeny.semver.checkers;

import static org.codegeny.semver.checkers.MethodCheckers.ADD_ANNOTATION_ELEMENT_WITHOUT_DEFAULT_VALUE;
import static org.codegeny.semver.checkers.MethodCheckers.ADD_ANNOTATION_ELEMENT_WITH_DEFAULT_VALUE;
import static org.codegeny.semver.checkers.MethodCheckers.ADD_DEFAULT_CLAUSE;
import static org.codegeny.semver.checkers.MethodCheckers.ADD_NON_DEFAULT_METHOD_IMPLEMENTABLE_BY_CLIENT;
import static org.codegeny.semver.checkers.MethodCheckers.ADD_STATIC_METHOD;
import static org.codegeny.semver.checkers.MethodCheckers.CHANGE_ABSTRACT_TO_DEFAULT;
import static org.codegeny.semver.checkers.MethodCheckers.CHANGE_ABSTRACT_TO_NON_ABSTRACT;
import static org.codegeny.semver.checkers.MethodCheckers.CHANGE_DEFAULT_CLAUSE;
import static org.codegeny.semver.checkers.MethodCheckers.CHANGE_DEFAULT_TO_ABSTRACT;
import static org.codegeny.semver.checkers.MethodCheckers.CHANGE_NON_ABSTRACT_TO_ABSTRACT;
import static org.codegeny.semver.checkers.MethodCheckers.CHANGE_RESULT_TYPE;
import static org.codegeny.semver.checkers.MethodCheckers.REMOVE_DEFAULT_CLAUSE;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.runners.Parameterized.Parameters;

public class MethodCheckersTest extends AbstractCheckersTest<Method, MethodCheckers> {

	static abstract class TestClass1 {
		
		abstract int age();
	}
	
	static abstract class TestClass2 {
		
		int age() {
			return 0;
		}
	}
	
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
		
		Class<?> attr() default Void.class;
	}
	
	@interface TestType8 {
		
		int attr() default 1;
	}

	@interface TestType9 {
		
		long attr() default 1;
	}

	@interface TestType10 {
		
		byte attr() default 1;
	}

	@interface TestType11 {
		
		short attr() default 1;
	}

	@interface TestType12 {
		
		boolean attr() default true;
	}

	@interface TestType13 {
		
		char attr() default 'c';
	}

	@interface TestType14 {
		
		float attr() default 1;
	}
	
	@interface TestType15 {
		
		double attr() default 1;
	}
	
	@interface TestType16 {
		
		String attr() default "test";
	}
	
	@interface TestType17 {
		
		Numbers attr() default Numbers.ONE;
	}
	
	@interface TestType18 {
		
		Class<?>[] attr() default Void.class;
	}
	
	@interface TestType19 {
		
		int[] attr() default 1;
	}

	@interface TestType20 {
		
		long[] attr() default 1;
	}

	@interface TestType21 {
		
		byte[] attr() default 1;
	}

	@interface TestType22 {
		
		short[] attr() default 1;
	}

	@interface TestType23 {
		
		boolean[] attr() default true;
	}

	@interface TestType24 {
		
		char[] attr() default 'c';
	}

	@interface TestType25 {
		
		float[] attr() default 1;
	}
	
	@interface TestType26 {
		
		double[] attr() default 1;
	}
	
	@interface TestType27 {
		
		String[] attr() default "test";
	}
	
	@interface TestType28 {
		
		Numbers[] attr() default Numbers.ONE;
	}

	enum Numbers {
		
		ONE, TWO
	}
	
//	static class TestType50 {
//		
//		int age() {
//			return 50;
//		}
//	}
//	
//	static class TestType51 {
//		
//		int age() {
//			return 50;
//		}
//	}
//	
//	static class TestType52 extends TestType51 {		
//	}
		
	@Parameters(name = "{0}")
	public static Collection<?> parameters() {
		Collection<Data<Method>> methods = methods(
				
			data(null, null),
			data(null, TestType1.class, ADD_NON_DEFAULT_METHOD_IMPLEMENTABLE_BY_CLIENT),
			data(TestType1.class, null),
			
			data(TestType1.class, TestType1.class),
			
			data(TestType1.class, TestType2.class, CHANGE_ABSTRACT_TO_DEFAULT),
			data(TestType2.class, TestType1.class, CHANGE_DEFAULT_TO_ABSTRACT),

			data(TestClass1.class, TestClass2.class, CHANGE_ABSTRACT_TO_NON_ABSTRACT),
			data(TestClass2.class, TestClass1.class, CHANGE_NON_ABSTRACT_TO_ABSTRACT),
			
			data(null, TestType3.class, ADD_STATIC_METHOD), 
			data(TestType1.class, TestType4.class, CHANGE_RESULT_TYPE),
			
			data(null, TestType5.class, ADD_ANNOTATION_ELEMENT_WITHOUT_DEFAULT_VALUE),
			data(null, TestType6.class, ADD_ANNOTATION_ELEMENT_WITH_DEFAULT_VALUE),
			
			data(TestType5.class, TestType6.class, ADD_DEFAULT_CLAUSE),
			data(TestType6.class, TestType5.class, REMOVE_DEFAULT_CLAUSE),
			data(TestType6.class, TestType7.class, CHANGE_DEFAULT_CLAUSE)
	
		);
		
		List<Class<?>> classes = Arrays.asList(
			TestType7.class, TestType8.class, TestType9.class, TestType10.class, TestType11.class, TestType12.class, TestType13.class, TestType14.class, TestType15.class,
			TestType16.class, TestType17.class, TestType18.class, TestType19.class, TestType20.class, TestType21.class, TestType22.class, TestType23.class, TestType24.class,
			TestType25.class, TestType26.class, TestType27.class, TestType28.class
		);
		
		classes.forEach(p -> methods.add(data(p, p).toMethod()));
		
		return methods;
	}
	
	public MethodCheckersTest() {
		super(MethodCheckers.class);
	}
}
