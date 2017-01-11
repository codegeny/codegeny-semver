package org.codegeny.semver;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

public interface Metadata {
	
	boolean isImplementedByClient(Class<?> klass);
	
	boolean isImplementedByClient(Method method);
	
	boolean isPublicAPI(Class<?> klass);
	
	boolean isPublicAPI(Member member);
	
	default Metadata or(Metadata other) {
		return new Metadata() {
			
			public boolean isImplementedByClient(Class<?> klass) {
				return Metadata.this.isImplementedByClient(klass) || other.isImplementedByClient(klass);
			}
			
			public boolean isImplementedByClient(Method method) {
				return Metadata.this.isImplementedByClient(method) || other.isImplementedByClient(method);
			}
			
			public boolean isPublicAPI(Class<?> klass) {
				return Metadata.this.isPublicAPI(klass) || other.isPublicAPI(klass);
			}
			
			public boolean isPublicAPI(Member member) {
				return Metadata.this.isPublicAPI(member) || other.isPublicAPI(member);
			}
		};
	}
}
