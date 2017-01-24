package org.codegeny.semver.classes2;

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
	
	class DecreateAccess {}
	
	enum DeleteEnumConstant { ONE, THREE }
	
	enum ReorderEnumConstants { THREE, ONE, TWO }
}
