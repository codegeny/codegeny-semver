package org.codegeny.semver.classes2;

public abstract class Methods {
	
	abstract void moveMethodUpWithImplementationNeeded();
	
	void moveMethodUpWithImplementationNotNeeded() {}
	
	@interface Annotation {
		
		int addAnnotationElementWithDefaultValue() default 1;
		
		int addAnnotationElementWithoutDefaultValue();
		
		int addDefaultClause() default 1;
		
		int removeDefaultClause();
		
		int changeDefaultClause() default 2;
	}
	
	interface Interface {
		
		default void changeAbstractToDefault() {}
		
		void changeDefaultToAbstract();
		
		default void addDefaultMethodImplementableByClient() {}
		
		default void addDefaultMethodNotImplementableByClient() {}
		
		void addNonDefaultMethodImplementableByClient();
		
		void addNonDefaultMethodNotImplementableByClient();
	}
	
	long changeResultType() {
		return 0;
	}
	
	void changeAbstractToNonAbstract() {}
	
	abstract void changeNonAbstractToAbstract();

}
