package org.codegeny.semver.checkers;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Collection;
import java.util.stream.Stream;

import org.junit.runners.Parameterized.Parameters;

public class AddUncheckedExceptionCheckerTest extends AbstractChangeCheckerTest<Method> {
	
	interface TestType1 {
		
		void testMethod();
	}
	
	interface TestType2 {
		
		void testMethod() throws IOException;
	}
	
	interface TestType3 {
		
		void testMethod() throws SQLException;
	}

	interface TestType4 {
	
		void testMethod() throws IOException, SQLException;
	}
	
	@Parameters(name = NAME)
	public static Collection<?> parameters() {
		return checks(
			patch(TestType1.class, TestType1.class), // no change
			major(TestType1.class, TestType2.class), // altered API and not compatible
			major(TestType1.class, TestType3.class), // altered API and not compatible
			major(TestType1.class, TestType4.class), // altered API and not compatible
			
			patch(TestType2.class, TestType1.class), // not applicable
			patch(TestType2.class, TestType2.class), // no change
			major(TestType2.class, TestType3.class), // altered API and not compatible
			major(TestType2.class, TestType4.class), // altered API and not compatible
			
			patch(TestType3.class, TestType1.class), // not applicable
			major(TestType3.class, TestType2.class), // altered API and not compatible
			patch(TestType3.class, TestType3.class), // no change
			major(TestType3.class, TestType4.class), // altered API and not compatible
			
			patch(TestType4.class, TestType1.class), // not applicable
			patch(TestType4.class, TestType2.class), // not applicable
			patch(TestType4.class, TestType3.class), // not applicable
			patch(TestType4.class, TestType4.class)  // no change
		);
	}
	
	public AddUncheckedExceptionCheckerTest() {
		super(new AddUncheckedExceptionChecker());
	}
	
	protected static Object major(Class<?> previous, Class<?> current) {
		return major(method(previous), method(current));
	}
	
	protected static Object minor(Class<?> previous, Class<?> current) {
		return minor(method(previous), method(current));
	}
	
	protected static Object patch(Class<?> previous, Class<?> current) {
		return patch(method(previous), method(current));
	}
	
	protected static Method method(Class<?> klass) {
		return Stream.of(klass.getDeclaredMethods()).findFirst().get();
	}
	
	
}
