package org.codegeny.semver;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isPrivate;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class DefaultMetadata implements Metadata {
	
	public boolean isImplementableByClient(Class<?> klass) {
		return !isFinal(klass.getModifiers());
	}
	
	public boolean isImplementableByClient(Method method) {
		return isImplementableByClient(method.getDeclaringClass()) && !isFinal(method.getModifiers());
	}
	
	public boolean isUsableByClient(Class<?> klass) {
		return !isPrivate(klass.getModifiers());
	}
	
	public boolean isUsableByClient(Member member) {
		return isUsableByClient(member.getDeclaringClass()) && !isPrivate(member.getModifiers());
	}	
}