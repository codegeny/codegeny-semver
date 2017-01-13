package org.codegeny.semver.checkers;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.codegeny.semver.Change.PATCH;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
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
public abstract class AbstractCheckersTest<T, C extends Enum<C> & Checker<? super T>> {
	
	protected static class ClassData extends Data<Class<?>> {

		public ClassData(Class<?> previous, Class<?> current, Object check) {
			super(previous, current, check);
		}
		
		public Data<Constructor<?>> toConstructor() {
			return new Data<>(
				getPrevious() != null && getPrevious().getDeclaredConstructors().length > 0 ? getPrevious().getDeclaredConstructors()[0] : null,
				getCurrent() != null && getCurrent().getDeclaredConstructors().length > 0 ? getCurrent().getDeclaredConstructors()[0] : null,
				getChecks()
			);
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
				getCurrent() == null ? null : Stream.of(getCurrent().getDeclaredMethods()).filter(m -> !m.getName().startsWith("$")).reduce((a, b) -> b).orElse(null),
				getChecks()
			);
		}
	}
	
	protected static class Data<T> {
		
		private final Object check;
		private final T previous, current;
				
		protected Data(T previous, T current, Object check) {
			this.previous = previous;
			this.current = current;
			this.check = check;
		}

		public Object getChecks() {
			return check;
		}

		public T getCurrent() {
			return current;
		}

		public T getPrevious() {
			return previous;
		}
		
		@Override
		public String toString() {
			return String.format("%s :: %s - %s", check, previous, current);
		}
	}
	
	protected static Collection<ClassData> classes(ClassData... data) {
		return new ArrayList<>(Arrays.asList(data));
	}
	
	protected static Collection<Data<Constructor<?>>> constructors(ClassData... data) {
		return Stream.of(data).map(ClassData::toConstructor).collect(toList());
	}
	
	protected static ClassData data(Class<?> previous, Class<?> current) {
		return data(previous, current, null);
	}
	
	protected static ClassData data(Class<?> previous, Class<?> current, Object check) {
		return new ClassData(previous, current, check);
	}
	
	protected static <Z> Data<Z> data(Z previous, Z current) {
		return data(previous, current, null);
	}
	
	protected static <Z> Data<Z> data(Z previous, Z current, Object check) {
		return new Data<>(previous, current, check);
	}
	
	protected static Collection<Data<Field>> fields(ClassData... data) {
		return Stream.of(data).map(ClassData::toField).collect(toList());
	}

	protected static Method method(Class<?> klass, String name) {
		return Stream.of(klass.getDeclaredMethods()).filter(m -> m.getName().equals(name)).findFirst().orElseThrow(RuntimeException::new);
	}
	
	protected static Method method(Class<?> klass) {
		return Stream.of(klass.getDeclaredMethods()).filter(m -> !m.getName().startsWith("$")).findFirst().orElseThrow(RuntimeException::new);
	}
	
	protected static Collection<Data<Method>> methods(ClassData... data) {
		return Stream.of(data).map(ClassData::toMethod).collect(toList());
	}
	
	private final Class<C> checkerClass;
	
	@Parameter
	public Data<T> data;
	
	private final Metadata metadata;
	
	protected AbstractCheckersTest(Class<C> checkerClass) {
		this(checkerClass, new DefaultMetadata());
	}
	
	protected AbstractCheckersTest(Class<C> checkerClass, Metadata metadata) {
		this.checkerClass = checkerClass;
		this.metadata = metadata;
	}

	@Test
	public void test() {
		Assert.assertEquals(
			Optional.ofNullable(data.getChecks()).map(Collections::singleton).orElseGet(Collections::emptySet),
			Stream.of(checkerClass.getEnumConstants()).filter(c -> c.check(data.getPrevious(), data.getCurrent(), metadata) != PATCH).collect(toSet())
		);
	}
}
