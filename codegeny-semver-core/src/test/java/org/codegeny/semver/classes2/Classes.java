package org.codegeny.semver.classes2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Set;

@SuppressWarnings("serial")
public class Classes {
	
	enum AddEnumConstant { ONE, TWO, THREE }
	
	static class AddType {}
	
	static class ChangeAbstractToNonAbstract {}
	
	static abstract class ChangeNonAbstractToAbstract {}
	
	static @interface ChangeKind {}
	
	static class ChangeFinalToNonFinal {}
	
	static final class ChangeNonFinalToFinal {}
	
	final class ChangeStaticToNonStatic {}
	
	static final class ChangeNonStaticToStatic {}
	
	public class IncreaseAccess {}
	
	class DecreaseAccess {}
	
	enum DeleteEnumConstant { ONE, THREE }
	
	enum ReorderEnumConstants { THREE, ONE, TWO }
	
	static abstract class ExpandSuperClassSet extends SimpleDateFormat {}
	
	static abstract class ContractSuperClassSet extends DateFormat {}
	
	interface ExpandSuperInterfacesSet extends Set<Object> {}
	
	interface ContractSuperInterfacesSet extends Collection<Object> {}
}
