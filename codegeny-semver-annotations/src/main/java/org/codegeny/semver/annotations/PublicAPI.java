package org.codegeny.semver.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface PublicAPI {
	
	boolean exclude() default false;
	
	boolean internal() default false;
}
