package org.codegeny.semver.classes1;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Set;

@SuppressWarnings("serial")
public class Classes {
	
	enum AddEnumConstant { ONE, TWO }
	
	static class DeleteType {}
	
	static abstract class ChangeAbstractToNonAbstract {}
	
	static class ChangeNonAbstractToAbstract {}
	
	static interface ChangeKind {}
	
	static final class ChangeFinalToNonFinal {}
	
	static class ChangeNonFinalToFinal {}
	
	static final class ChangeStaticToNonStatic {}
	
	final class ChangeNonStaticToStatic {}
	
	protected class IncreaseAccess {}
	
	protected class DecreaseAccess {}
	
	enum DeleteEnumConstant { ONE, TWO, THREE }
	
	enum ReorderEnumConstants { ONE, TWO, THREE }
	
	static abstract class ExpandSuperClassSet extends DateFormat {}
	
	static abstract class ContractSuperClassSet extends SimpleDateFormat {}
	
	interface ExpandSuperInterfacesSet extends Collection<Object> {}
	
	interface ContractSuperInterfacesSet extends Set<Object> {}
}
