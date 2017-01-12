package org.codegeny.semver.checkers;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.codegeny.semver.Change.PATCH;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.codegeny.semver.Checker;
import org.codegeny.semver.DefaultMetadata;
import org.codegeny.semver.Metadata;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;

@RunWith(Parameterized.class)
public abstract class AbstractCheckersTest<T, C extends Enum<C> & Checker<T>> {
	
	protected static class ClassData extends Data<Class<?>> {

		public ClassData(Class<?> previous, Class<?> current, Object... checks) {
			super(previous, current, checks);
		}
		
		public Data<Field> toField() {
			return new Data<>(
				getPrevious() != null && getPrevious().getDeclaredFields().length > 0 ? getPrevious().getDeclaredFields()[0] : null,
				getCurrent() != null && getCurrent().getDeclaredFields().length > 0 ? getCurrent().getDeclaredFields()[0] : null,
				getChecks()
			);
		}
		
		public Data<Method> toMethod() {
			return new Data<>(
				getPrevious() == null ? null : Stream.of(getPrevious().getDeclaredMethods()).filter(m -> !m.getName().startsWith("$")).findFirst().orElse(null),
				getCurrent() == null ? null : Stream.of(getCurrent().getDeclaredMethods()).filter(m -> !m.getName().startsWith("$")).findFirst().orElse(null),
				getChecks()
			);
		}
	}
	
	private static class Data<T> {
		
		private final Set<?> checks;
		private final T previous, current;
		
		public Data(T previous, T current, Object... checks) {
			this(previous, current, new HashSet<>(Arrays.asList(checks)));
		}
		
		protected Data(T previous, T current, Set<?> checks) {
			this.previous = previous;
			this.current = current;
			this.checks = checks;
		}

		public Set<?> getChecks() {
			return checks;
		}

		public T getCurrent() {
			return current;
		}

		public T getPrevious() {
			return previous;
		}
		
		@Override
		public String toString() {
			return String.format("%s :: %s - %s", checks, previous, current);
		}
	}
	
	protected static Collection<?> classes(ClassData... data) {
		return Arrays.asList(data);
	}
	
	protected static ClassData data(Class<?> previous, Class<?> current, Object... checks) {
		return new ClassData(previous, current, checks);
	}
	
	protected static Collection<?> fields(ClassData... data) {
		return Stream.of(data).map(ClassData::toField).collect(toList());
	}

	protected static Collection<?> methods(ClassData... data) {
		return Stream.of(data).map(ClassData::toMethod).collect(toList());
	}
	
	private final Class<C> checkerClass;
	private final Metadata metadata;
	
	@Parameter
	public Data<T> data;
	
	protected AbstractCheckersTest(Class<C> checkerClass, Metadata metadata) {
		this.checkerClass = checkerClass;
		this.metadata = metadata;
	}
	
	protected AbstractCheckersTest(Class<C> checkerClass) {
		this(checkerClass, new DefaultMetadata());
	}

	@Test
	public void test() {
		Assert.assertEquals(data.getChecks(), Stream.of(checkerClass.getEnumConstants()).filter(c -> c.check(data.getPrevious(), data.getCurrent(), metadata) != PATCH).collect(toSet()));
	}
}
