package org.codegeny.semver.checkers;

import java.util.Arrays;
import java.util.Collection;

import org.codegeny.semver.Change;
import org.codegeny.semver.ChangeChecker;
import org.codegeny.semver.LoggerAware;
import org.codegeny.semver.Metadata;
import org.codegeny.semver.MetadataAware;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;

@RunWith(Parameterized.class)
public abstract class AbstractChangeCheckerTest<T> {
	
	protected static final String NAME = "[{index}] {0} = {1} :: {2}";

	protected static <T> Object check(Change expectedChange, T previous, T current) {
		return new Object[] { expectedChange, previous, current };
	}
	
	protected static Collection<?> checks(Object... dataSet) {
		return Arrays.asList(dataSet);
	}
	
	protected static <T> Object major(T previous, T current) {
		return check(Change.MAJOR, previous, current);
	}
	
	protected static <T> Object minor(T previous, T current) {
		return check(Change.MINOR, previous, current);
	}
	
	protected static <T> Object patch(T previous, T current) {
		return check(Change.PATCH, previous, current);
	}
	
	private final ChangeChecker<T> checker;
	
	@Parameter(2)
	public T current;
	
	@Parameter(0)
	public Change expectedChange;
	
	@Parameter(1)
	public T previous;

	protected AbstractChangeCheckerTest(ChangeChecker<T> checker) {
		this(checker, new Metadata.Default());
	}
	
	protected AbstractChangeCheckerTest(ChangeChecker<T> checker, Metadata metadata) {
		this.checker = checker;
		if (checker instanceof MetadataAware) {
			((MetadataAware) checker).setMetadata(metadata);		
		}
		if (checker instanceof LoggerAware) {
			((LoggerAware) checker).setLogger((f, a) -> System.out.printf(f, a).println());
		}
	}
	
	@Test
	public void test() {
		Assert.assertEquals(expectedChange, checker.check(previous, current));
	}
}
