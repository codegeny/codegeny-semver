package org.codegeny.semver;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

public interface Metadata {
	
	boolean isImplementableByClient(Class<?> klass);
	
	boolean isImplementableByClient(Method method);
	
	boolean isUsableByClient(Class<?> klass);
	
	boolean isUsableByClient(Member member);
	
	default Metadata or(Metadata other) {
		return new Metadata() {
			
			public boolean isImplementableByClient(Class<?> klass) {
				return Metadata.this.isImplementableByClient(klass) || other.isImplementableByClient(klass);
			}
			
			public boolean isImplementableByClient(Method method) {
				return Metadata.this.isImplementableByClient(method) || other.isImplementableByClient(method);
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
