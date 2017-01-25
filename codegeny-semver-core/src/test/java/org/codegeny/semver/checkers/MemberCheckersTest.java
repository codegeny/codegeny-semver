package org.codegeny.semver.checkers;

import static org.codegeny.semver.checkers.MemberCheckers.*;

import java.lang.reflect.Member;
import java.util.Collection;

import org.junit.Ignore;
import org.junit.runners.Parameterized.Parameters;

@Ignore
public class MemberCheckersTest extends AbstractCheckersTest<Member, MemberCheckers> {
	
	static class TestType1 {
		
		int m;
	}
	
	static class TestType2 {
		
		public int m;
	}
	
	static class TestType3 {
		
		protected int m;
	}
	
	static class TestType4 {
		
		@SuppressWarnings("unused")
		private int m;
	}
	
	static class TestType5 {
		
		static int m;
	}
		
	@Parameters(name = "{0}")
	public static Collection<?> parameters() {
		return fields(
			data(null, null),
			data(null, TestType1.class),
			data(TestType1.class, null, DELETE_MEMBER),
			data(TestType1.class, TestType1.class),
			data(TestType2.class, TestType2.class),
			data(TestType5.class, TestType5.class),
			
			data(TestType1.class, TestType2.class, INCREASE_ACCESS),
			data(TestType1.class, TestType3.class, INCREASE_ACCESS),
			data(TestType1.class, TestType4.class, DECREASE_ACCESS),

			data(TestType2.class, TestType1.class, DECREASE_ACCESS),
			data(TestType2.class, TestType3.class, DECREASE_ACCESS),
			data(TestType2.class, TestType4.class, DECREASE_ACCESS),

			data(TestType3.class, TestType1.class, DECREASE_ACCESS),
			data(TestType3.class, TestType2.class, INCREASE_ACCESS),
			data(TestType3.class, TestType4.class, DECREASE_ACCESS),

			data(TestType4.class, TestType1.class, INCREASE_ACCESS),
			data(TestType4.class, TestType2.class, INCREASE_ACCESS),
			data(TestType4.class, TestType3.class, INCREASE_ACCESS),
			
			data(TestType1.class, TestType5.class, CHANGE_NON_STATIC_TO_STATIC),
			data(TestType5.class, TestType1.class, CHANGE_STATIC_TO_NON_STATIC)
		);
	}
	
	public MemberCheckersTest() {
		super(MemberCheckers.class);
	}
}
