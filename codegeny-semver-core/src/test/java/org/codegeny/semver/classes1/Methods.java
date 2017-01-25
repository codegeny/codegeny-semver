package org.codegeny.semver.classes1;

import org.codegeny.semver.classes1.Methods.UltimateAnnotation.Color;

public abstract class Methods {
	
	void moveMethodDown() {}
	
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
		
		int addDefaultClause();
		
		int removeDefaultClause() default 1;
		
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
				stringValue = "a"
		);
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
	
	public abstract class Methods2 extends Methods {

		abstract void moveMethodUpWithImplementationNeeded();
		
		void moveMethodUpWithImplementationNotNeeded() {}
	}
}
