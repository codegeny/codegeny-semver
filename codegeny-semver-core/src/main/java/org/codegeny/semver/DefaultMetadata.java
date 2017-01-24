package org.codegeny.semver;

import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isPrivate;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class DefaultMetadata implements Metadata {
	
	public boolean needsImplementationByClient(Class<?> klass) {
		return isAbstract(klass.getModifiers());
	}
	
	public boolean needsImplementationByClient(Method method) {
		return isAbstract(method.getModifiers());
	}
	
	public boolean isUsableByClient(Class<?> klass) {
		return !isPrivate(klass.getModifiers());
	}
	
	public boolean isUsableByClient(Member member) {
		return isUsableByClient(member.getDeclaringClass()) && !isPrivate(member.getModifiers());
	}	
}