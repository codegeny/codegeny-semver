package org.codegeny.semver.classes2;

import org.codegeny.semver.classes2.Methods.UltimateAnnotation.Color;

public abstract class Methods {
	
	abstract void moveMethodUpWithImplementationNeeded();
	
	void moveMethodUpWithImplementationNotNeeded() {}
	
	@interface UltimateAnnotation {
		
			enum Color { RED, GREEN, BLUE }
			
			boolean booleanValue();
			
			byte byteValue();
			
			short shortValue();
			
			char charValue();
			
			int intValue();
			
			long longValue();
			
			float floatValue();
			
			double doubleValue();
			
			Class<? extends Number> classValue();
			
			String stringValue();
			
			Color enumValue();
			
			boolean[] booleanArray();
			
			byte[] byteArray();
			
			short[] shortArray();
			
			char[] charArray();
			
			int[] intArray();
			
			long[] longArray();
			
			float[] floatArray();
			
			double[] doubleArray();
			
			Class<? extends Number>[] classArray();
			
			String[] stringArray();
			
			Color[] enumArray();
	}
	
	@interface Annotation {
		
		int addAnnotationElementWithDefaultValue() default 1;
		
		int addAnnotationElementWithoutDefaultValue();
		
		int addDefaultClause() default 1;
		
		int removeDefaultClause();
		
		UltimateAnnotation changeDefaultClause() default @UltimateAnnotation(
				booleanArray = true,
				booleanValue = true,
				byteArray = 1,
				byteValue = 1,
				charArray = 'a',
				charValue = 'b',
				classArray = Long.class,
				classValue = Long.class,
				doubleArray = 1,
				doubleValue = 1,
				enumArray = Color.BLUE,
				enumValue = Color.BLUE,
				floatArray = 1,
				floatValue = 1,
				intArray = 1,
				intValue = 1,
				longArray = 1,
				longValue = 1,
				shortArray = 1,
				shortValue = 1,
				stringArray = "a",
				stringValue = "b"
		);
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

	public abstract class Methods2 extends Methods {

		void moveMethodDown() {}
	}
}
