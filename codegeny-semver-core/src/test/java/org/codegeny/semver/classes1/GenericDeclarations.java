package org.codegeny.semver.classes1;

import java.util.Map;
import java.util.Set;

public class GenericDeclarations {

	void addTypeParameterWhenNoParametersExist() {};
	
	<E> void addTypeParameterWhenOtherParametersExist() {};
	
	<E extends CharSequence, F extends Map<? extends E[], ? super Set<?>>, Z extends Number> void changeTypeParametersBounds() {}
	
	<E, Z> void deleteTypeParameter() {}
}
