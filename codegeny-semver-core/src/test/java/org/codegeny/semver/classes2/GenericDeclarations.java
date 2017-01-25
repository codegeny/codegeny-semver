package org.codegeny.semver.classes2;

import java.util.Map;
import java.util.Set;

public class GenericDeclarations {

	<E> void addTypeParameterWhenNoParametersExist() {};
	
	<E, T> void addTypeParameterWhenOtherParametersExist() {};
	
	<E extends CharSequence, F extends Map<? extends E[], ? super Set<?>>, Z extends CharSequence> void changeTypeParametersBounds() {}
	
	<E> void deleteTypeParameter() {}
}
