package org.codegeny.semver.checkers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

import org.codegeny.semver.Change;
import org.codegeny.semver.Checker;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;

@RunWith(Parameterized.class)
public abstract class AbstractChangeCheckerTest<T> {
	
	protected static class ClassData extends Data<Class<?>> {

		public ClassData(Change expectedChange, Class<?> previous, Class<?> current) {
			super(expectedChange, previous, current);
		}
		
		public ConstructorData constructor() {
			return new ConstructorData(getExpectedChange(), Stream.of(getPrevious().getDeclaredConstructors()).findFirst().orElse(null), Stream.of(getCurrent().getDeclaredConstructors()).findFirst().orElse(null));
		}
		
		public FieldData field() {
			return new FieldData(getExpectedChange(), Stream.of(getPrevious().getDeclaredFields()).findFirst().orElse(null), Stream.of(getCurrent().getDeclaredFields()).findFirst().orElse(null));
		}
		
		public MethodData method() {
			return new MethodData(getExpectedChange(), Stream.of(getPrevious().getDeclaredMethods()).findFirst().orElse(null), Stream.of(getCurrent().getDeclaredMethods()).findFirst().orElse(null));
		}
	}
	
	protected static class ConstructorData extends Data<Constructor<?>> {

		public ConstructorData(Change expectedChange, Constructor<?> previous, Constructor<?> current) {
			super(expectedChange, previous, current);
		}
	}
	
	protected abstract static class Data<T> {
		
		private final T current;
		private final Change expectedChange;
		private final T previous;
		
		public Data(Change expectedChange, T previous, T current) {
			this.expectedChange = expectedChange;
			this.previous = previous;
			this.current = current;
		}

		public T getCurrent() {
			return current;
		}

		public Change getExpectedChange() {
			return expectedChange;
		}

		public T getPrevious() {
			return previous;
		}
	}
	
	protected static class FieldData extends Data<Field> {

		public FieldData(Change expectedChange, Field previous, Field current) {
			super(expectedChange, previous, current);
		}
	}
	
	protected static class MethodData extends Data<Method> {

		public MethodData(Change expectedChange, Method previous, Method current) {
			super(expectedChange, previous, current);
		}
	}
	
	protected static final String NAME = "[{index}] {0} = {1} :: {2}";

	private static ClassData check(Change expectedChange, Class<?> previous, Class<?> current) {
		return new ClassData(expectedChange, previous, current);
	}
	
	protected static Collection<?> checks(Data<?>... dataSet) {
		return Arrays.asList(dataSet);
	}
	
	protected static Collection<?> classes(ClassData... dataSet) {
		return checks(dataSet);
	}
	
	protected static Collection<?> constructors(ClassData... dataSet) {
		return transform(ClassData::constructor, dataSet);
	}
	
	protected static Collection<?> fields(ClassData... dataSet) {
		return transform(ClassData::field, dataSet);
	}
	
	protected static ClassData major(Class<?> previous, Class<?> current) {
		return check(Change.MAJOR, previous, current);
	}
	
	protected static Collection<?> methods(ClassData... dataSet) {
		return transform(ClassData::method, dataSet);
	}
	
	protected static ClassData minor(Class<?> previous, Class<?> current) {
		return check(Change.MINOR, previous, current);
	}
	
	protected static ClassData patch(Class<?> previous, Class<?> current) {
		return check(Change.PATCH, previous, current);
	}
	
	private static Collection<?> transform(Function<ClassData, Data<?>> function, ClassData... dataSet) {
		return checks(Stream.of(dataSet).map(function).toArray(i -> new Data<?>[i]));
	}
	
	private final Checker<T> checker;
	
	@Parameter
	public Data<T> data;
	
	protected AbstractChangeCheckerTest(Checker<T> checker) {
		this.checker = checker;
	}
	
	@Test
	public void test() {
		Assert.assertEquals(data.getExpectedChange(), checker.check(data.getPrevious(), data.getCurrent()));
	}
}
