package org.codegeny.semver;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

public interface Metadata {
	
	boolean needsImplementationByClient(Class<?> klass);
	
	boolean needsImplementationByClient(Method method);
	
	boolean isUsableByClient(Class<?> klass);
	
	boolean isUsableByClient(Member member);
	
	default Metadata or(Metadata other) {
		return new Metadata() {
			
			public boolean needsImplementationByClient(Class<?> klass) {
				return Metadata.this.needsImplementationByClient(klass) || other.needsImplementationByClient(klass);
			}
			
			public boolean needsImplementationByClient(Method method) {
				return Metadata.this.needsImplementationByClient(method) || other.needsImplementationByClient(method);
			}
			
			public boolean isUsableByClient(Class<?> klass) {
				return Metadata.this.isUsableByClient(klass) || other.isUsableByClient(klass);
			}
			
			public boolean isUsableByClient(Member member) {
				return Metadata.this.isUsableByClient(member) || other.isUsableByClient(member);
			}
		};
	}
}
