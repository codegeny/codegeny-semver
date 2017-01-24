package org.codegeny.semver.classes1;

public abstract class Methods {
	
	void moveMethodDown() {}
	
	@interface Annotation {
		
		int addDefaultClause();
		
		int removeDefaultClause() default 1;
		
		int changeDefaultClause() default 1;
	}
	
	interface Interface {
		
		void changeAbstractToDefault();
		
		default void changeDefaultToAbstract() {}
		
	}
	
	int changeResultType() {
		return 0;
	}
	
	abstract void changeAbstractToNonAbstract();
	
	void changeNonAbstractToAbstract() {}
}
