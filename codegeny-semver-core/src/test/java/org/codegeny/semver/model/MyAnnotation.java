package org.codegeny.semver.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {

	String bluh();
	
	Class<?>[] blarg() default {void.class, int.class};
	
	int bh();
	
	int[] ffhf();
}
