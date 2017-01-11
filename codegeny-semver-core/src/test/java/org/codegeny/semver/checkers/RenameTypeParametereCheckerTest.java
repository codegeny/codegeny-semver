package org.codegeny.semver.checkers;

import java.lang.reflect.GenericDeclaration;
import java.util.Collection;
import java.util.function.Supplier;

import org.junit.runners.Parameterized.Parameters;

public class RenameTypeParametereCheckerTest extends AbstractChangeCheckerTest<GenericDeclaration> {
	
	interface Test1<A, E extends Supplier<C>, C> {}
	interface Test2<E, A extends Supplier<D>, D> {}
	interface Test3<A, E extends Supplier<A>, C> {}
	
	@Parameters(name = NAME)
	public static Collection<?> parameters() {
		return classes(
			major(Test1.class, Test2.class), //
			major(Test2.class, Test3.class), //
			major(Test3.class, Test1.class)  //
		);
	}

	public RenameTypeParametereCheckerTest() {
		super(GenericDeclarationCheckers.RENAME_TYPE_PARAMETER);
	}
}
