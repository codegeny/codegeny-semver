package org.codegeny.semver.annotations;

import org.codegeny.semver.model.XClass;
import org.codegeny.semver.model.XMethod;
import org.codegeny.semver.rule.MetaDataProvider;

public class MetaDataProviderImpl implements MetaDataProvider {

	@Override
	public boolean isPublic(XClass klass) {
		return klass.getAnnotations().stream()
			.filter(a -> a.getAnnotationType().getName().equals(PublicAPI.class.getName()))
			.findAny()
			.map(a -> a.getAttributes().get("exclude"))
			.map(a -> false)
			.orElse(false);
	}

	@Override
	public boolean isPublic(XMethod method) {
		throw new UnsupportedOperationException();
	}	
}
