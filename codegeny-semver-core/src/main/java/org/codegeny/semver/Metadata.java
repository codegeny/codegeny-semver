package org.codegeny.semver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public interface Metadata {

	class Default implements Metadata {}
	
	default Metadata and(Metadata other) {
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
			
			public boolean isPublicAPI(Field field) {
				return Metadata.this.isPublicAPI(field) || other.isPublicAPI(field);
			}
			
			public boolean isPublicAPI(Method method) {
				return Metadata.this.isPublicAPI(method) || other.isPublicAPI(method);
			}
		};
	}
	
	default boolean isImplementedByClient(Class<?> klass) {
		return !Modifier.isFinal(klass.getModifiers());
	}
	
	default boolean isImplementedByClient(Method method) {
		return isImplementedByClient(method.getDeclaringClass()) && !Modifier.isFinal(method.getModifiers());
	}
	
	default boolean isPublicAPI(Class<?> klass) {
		return Modifier.isPublic(klass.getModifiers());
	}
	
	default boolean isPublicAPI(Field field) {
		return isPublicAPI(field.getDeclaringClass()) && !Modifier.isPrivate(field.getModifiers());
	}
	
	default boolean isPublicAPI(Method method) {
		return isPublicAPI(method.getDeclaringClass()) && !Modifier.isPrivate(method.getModifiers());
	}
	
	default boolean isPublicAPI(Constructor<?> constructor) {
		return isPublicAPI(constructor.getDeclaringClass()) && !Modifier.isPrivate(constructor.getModifiers());
	}
}
