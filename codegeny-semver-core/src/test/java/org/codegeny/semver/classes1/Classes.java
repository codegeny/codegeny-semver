package org.codegeny.semver.classes1;

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
	
	protected class DecreateAccess {}
	
	enum DeleteEnumConstant { ONE, TWO, THREE }
	
	enum ReorderEnumConstants { ONE, TWO, THREE }
}
