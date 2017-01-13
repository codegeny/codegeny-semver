package org.codegeny.semver.checkers;

import static org.codegeny.semver.checkers.GenericDeclarationCheckers.ADD_TYPE_PARAMETER_WHEN_NO_PARAMETERS_EXIST;
import static org.codegeny.semver.checkers.GenericDeclarationCheckers.ADD_TYPE_PARAMETER_WHEN_OTHER_PARAMETERS_EXIST;
import static org.codegeny.semver.checkers.GenericDeclarationCheckers.CHANGE_TYPE_PARAMETERS_BOUNDS;
import static org.codegeny.semver.checkers.GenericDeclarationCheckers.DELETE_TYPE_PARAMETER;

import java.lang.reflect.GenericDeclaration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.runners.Parameterized.Parameters;

public class GenericDeclarationCheckersTest extends AbstractCheckersTest<GenericDeclaration, GenericDeclarationCheckers> {
	
	interface TestType1 {
		
		void m();
	}
	
	interface TestType2 {
		
		<T> void m();
	}
	
	interface TestType3 {
		
		<T, E> void m();
	}
	
	interface TestType4 {
		
		<T extends CharSequence> void m();
	}
	
	interface TestType5 {
		
		<T extends Map<T, ?>, Z extends Set<T[]>> void m();
	}
	
	interface TestType6 {
		
		<R extends Map<R, ?>, S extends Set<R[]>> void m();
	}
	
	interface TestType7 {
		
		<R extends Map<R, ?>, S extends Set<S[]>> void m();
	}
	
	interface TestType8 {
		
		<I extends Collection<O>, O extends Collection<I>> void m();
	}
	
	interface TestType9 {
		
		<O extends Collection<I>, I extends Collection<O>> void m();
	}
	
	interface TestType10 {
		
		<E, T extends Set<?>> void m();
	}
	
	interface TestType11 {
		
		<E, T extends Set<E>> void m();
	}
	
	interface TestType12 {
		
		<E, T extends Set<E[]>> void m();
	}
	
	interface TestType13 {
		
		<E, T extends Set<Set<E>>> void m();
	}
	
	interface TestType14 {
		
		<E, T extends Set<Number>> void m();
	}
	
	interface TestType15 {
		
		<E extends Set<? extends Number>> void m();
	}
	
	interface TestType16 {
		
		<E extends Set<? super Number>> void m();
	}
	
	interface TestType17 {
		
		<E, T extends List<?>> void m();
	}
	
	interface TestType20<E> {
		
		<Z, T extends Set<E>> void m();
	}
	
	interface TestType21 {
		
		<Z, T extends Set<Z>> void m();
	}
	
	interface TestType22<E> {
		
		@SuppressWarnings("hiding")
		<E, T extends Set<E>> void m();
	}
	
	static class TestType30<E> {
		
		abstract class SubType1<I extends E> {
			
			abstract <O extends I> void m();
		}

		abstract class SubType2<I extends E> {
			
			abstract <O extends I> void m();
		}
	}
	
	static class TestType31<E> {
		
		abstract class SubType<I extends E> {
			
			abstract <O extends I> void m();
			abstract <O extends I> void n();
		}
	}
	
	interface TestType32 {
		
		<E extends TestType30<Number>.SubType1<Long>> void m();
	}
	
	interface TestType33 {
		
		<E extends TestType30<Number>.SubType2<Long>> void m();
	}
	
	@Parameters(name = "{0}")
	public static Collection<?> parameters() {
		return methods(
			data(null, null),
			data(null, TestType1.class),
			data(TestType1.class, null),
			data(TestType1.class, TestType1.class),
			data(TestType1.class, TestType2.class, ADD_TYPE_PARAMETER_WHEN_NO_PARAMETERS_EXIST),
			data(TestType2.class, TestType3.class, ADD_TYPE_PARAMETER_WHEN_OTHER_PARAMETERS_EXIST),
			data(TestType2.class, TestType1.class, DELETE_TYPE_PARAMETER),
			data(TestType2.class, TestType4.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType5.class, TestType6.class),
			data(TestType6.class, TestType7.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType8.class, TestType9.class),
			
			data(TestType10.class, TestType10.class),
			data(TestType10.class, TestType11.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType10.class, TestType12.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType10.class, TestType13.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType10.class, TestType14.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType10.class, TestType17.class, CHANGE_TYPE_PARAMETERS_BOUNDS),

			data(TestType11.class, TestType10.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType11.class, TestType11.class),
			data(TestType11.class, TestType12.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType11.class, TestType13.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType11.class, TestType14.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType11.class, TestType17.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			
			data(TestType12.class, TestType10.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType12.class, TestType11.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType12.class, TestType12.class),
			data(TestType12.class, TestType13.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType12.class, TestType14.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType12.class, TestType17.class, CHANGE_TYPE_PARAMETERS_BOUNDS),

			data(TestType13.class, TestType10.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType13.class, TestType11.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType13.class, TestType12.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType13.class, TestType13.class),
			data(TestType13.class, TestType14.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType13.class, TestType17.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			
			data(TestType14.class, TestType10.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType14.class, TestType11.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType14.class, TestType12.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType14.class, TestType13.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType14.class, TestType14.class),
			data(TestType14.class, TestType17.class, CHANGE_TYPE_PARAMETERS_BOUNDS),

			data(TestType17.class, TestType10.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType17.class, TestType11.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType17.class, TestType12.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType17.class, TestType13.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType17.class, TestType14.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType17.class, TestType17.class),	
		
			data(TestType15.class, TestType15.class),
			data(TestType15.class, TestType16.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType16.class, TestType16.class),
			data(TestType16.class, TestType15.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			
			data(TestType20.class, TestType20.class),
			data(TestType20.class, TestType21.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType20.class, TestType22.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			
			data(TestType21.class, TestType20.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType21.class, TestType21.class),
			data(TestType21.class, TestType22.class),
			
			data(TestType22.class, TestType20.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType22.class, TestType21.class),
			data(TestType22.class, TestType22.class),
			
			data(TestType30.SubType1.class, TestType30.SubType2.class, CHANGE_TYPE_PARAMETERS_BOUNDS),
			data(TestType31.SubType.class, TestType31.SubType.class),
			
			data(TestType32.class, TestType33.class, CHANGE_TYPE_PARAMETERS_BOUNDS)
			
		);
	}
	
	public GenericDeclarationCheckersTest() {
		super(GenericDeclarationCheckers.class);
	}
}
