package org.codegeny.semver.classes2;

import java.util.Set;

public class Executables {
	
	void removeCheckedException() {}
	
	void addCheckedException() throws Exception {}
	
	void addRuntimeException() throws RuntimeException {}
	
	void removeRuntimeException() {}
	
	void addError() throws Error {}
	
	void removeError() {}
	
	void changeArrayToVarargs(Object... values) {}
	
	void changeVarargsToArray(Object[] values) {}
	
	void changeGenericParameterType(Set<Long> v) {}
}
