package org.codegeny.semver.annotations;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import org.codegeny.semver.Metadata;

public class PublicAPIMetadata implements Metadata {

	@Override
	public boolean needsImplementationByClient(Class<?> klass) {
		PublicAPI publicAPI = klass.getAnnotation(PublicAPI.class);
		if (publicAPI != null) {
			return !publicAPI.internal();
		}
		publicAPI = klass.getPackage().getAnnotation(PublicAPI.class);
		if (publicAPI != null) {
			return !publicAPI.internal();
		}
		return false;
	}

	@Override
	public boolean needsImplementationByClient(Method method) {
		PublicAPI publicAPI = method.getAnnotation(PublicAPI.class);
		if (publicAPI != null) {
			return !publicAPI.internal();
		}
		return isUsableByClient(method.getDeclaringClass());
	}

	@Override
	public boolean isUsableByClient(Class<?> klass) {
		PublicAPI publicAPI = klass.getAnnotation(PublicAPI.class);
		if (publicAPI != null) {
			return !publicAPI.exclude();
		}
		publicAPI = klass.getPackage().getAnnotation(PublicAPI.class);
		if (publicAPI != null) {
			return !publicAPI.exclude();
		}
		return false;
	}

	@Override
	public boolean isUsableByClient(Member field) {
		PublicAPI publicAPI = ((AnnotatedElement) field).getAnnotation(PublicAPI.class);
		if (publicAPI != null) {
			return !publicAPI.exclude();
		}
		return isUsableByClient(field.getDeclaringClass());
	}
}
