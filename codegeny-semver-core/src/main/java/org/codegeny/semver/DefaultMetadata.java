package org.codegeny.semver;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class DefaultMetadata implements Metadata {
	
	public boolean isImplementedByClient(Class<?> klass) {
		return !Modifier.isFinal(klass.getModifiers());
	}
	
	public boolean isImplementedByClient(Method method) {
		return isImplementedByClient(method.getDeclaringClass()) && !Modifier.isFinal(method.getModifiers());
	}
	
	public boolean isPublicAPI(Class<?> klass) {
		return Modifier.isPublic(klass.getModifiers());
	}
	
	public boolean isPublicAPI(Member member) {
		return isPublicAPI(member.getDeclaringClass()) && !Modifier.isPrivate(member.getModifiers());
	}	
}