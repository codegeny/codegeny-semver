package org.codegeny.semver.checkers;

import static org.codegeny.semver.checkers.ClassCheckers.ADD_TYPE;
import static org.codegeny.semver.checkers.ClassCheckers.DECREASE_ACCESS;
import static org.codegeny.semver.checkers.ClassCheckers.DELETE_TYPE;
import static org.codegeny.semver.checkers.ClassCheckers.ENUM_ADD_CONSTANT;
import static org.codegeny.semver.checkers.ClassCheckers.ENUM_DELETE_CONSTANT;
import static org.codegeny.semver.checkers.ClassCheckers.ENUM_REORDER_CONSTANTS;
import static org.codegeny.semver.checkers.ClassCheckers.INCREASE_ACCESS;
import static org.codegeny.semver.checkers.ClassCheckers.TYPE_CHANGE_ABSTRACT_TO_NON_ABSTRACT;
import static org.codegeny.semver.checkers.ClassCheckers.TYPE_CHANGE_FINAL_TO_NON_FINAL;
import static org.codegeny.semver.checkers.ClassCheckers.TYPE_CHANGE_KIND;
import static org.codegeny.semver.checkers.ClassCheckers.TYPE_CHANGE_NON_ABSTRACT_TO_ABSTRACT;
import static org.codegeny.semver.checkers.ClassCheckers.TYPE_CHANGE_NON_FINAL_TO_FINAL;

import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

public class ClassCheckersTest extends AbstractCheckersTest<Class<?>, ClassCheckers> {
	
	public enum TestType1 {
		
		ONE
	}
	
	public enum TestType2 {
		
		ONE, TWO
	}
	
	public enum TestType3 {
		
		TWO, ONE
	}
	
	public interface TestType4 {}
	
	public static class TestType5 {}
	
	public @interface TestType6 {}
	
	protected static class TestType7 {}
	
	public abstract static class TestType8 {}
	
	public final static class TestType9 {}
	
	@Parameters(name = "{0}")
	public static Collection<?> parameters() {
		return classes(
			data(null, null),
			data(null, TestType1.class, ADD_TYPE),
			data(TestType1.class, null, DELETE_TYPE),
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

			data(TestType5.class, TestType7.class, DECREASE_ACCESS),
			data(TestType7.class, TestType5.class, INCREASE_ACCESS),
			
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
