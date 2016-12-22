package org.codegeny.semver.rule;

import org.codegeny.semver.model.XClass;
import org.codegeny.semver.model.XMethod;

public enum DefaultMetaDataProvider implements MetaDataProvider {
	
	INSTANCE;

	@Override
	public boolean isPublic(XClass klass) {
		return false;
	}

	@Override
	public boolean isPublic(XMethod method) {
		return false;
	}
}
