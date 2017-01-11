package org.codegeny.semver.checkers;

import java.io.IOException;
import java.lang.reflect.Executable;
import java.sql.SQLException;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

public class RemoveCheckedExceptionCheckerTest extends AbstractChangeCheckerTest<Executable> {
	
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
		return methods(
			patch(TestType1.class, TestType1.class), // no change
			patch(TestType1.class, TestType2.class), // not applicable
			patch(TestType1.class, TestType3.class), // not applicable
			patch(TestType1.class, TestType4.class), // not applicable
			
			major(TestType2.class, TestType1.class), // altered API and not compatible
			patch(TestType2.class, TestType2.class), // no change
			major(TestType2.class, TestType3.class), // altered API and not compatible
			patch(TestType2.class, TestType4.class), // not applicable
			
			major(TestType3.class, TestType1.class), // altered API and not compatible
			major(TestType3.class, TestType2.class), // altered API and not compatible
			patch(TestType3.class, TestType3.class), // no change
			patch(TestType3.class, TestType4.class), // not applicable
			
			major(TestType4.class, TestType1.class), // altered API and not compatible
			major(TestType4.class, TestType2.class), // altered API and not compatible
			major(TestType4.class, TestType3.class), // altered API and not compatible
			patch(TestType4.class, TestType4.class)  // no change
		);
	}
	
	public RemoveCheckedExceptionCheckerTest() {
		super(ExecutableCheckers.REMOVE_CHECKED_EXCEPTION);
	}
}