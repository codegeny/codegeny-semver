package org.codegeny.semver.rule;

import org.codegeny.semver.Change;

public interface Rule<T> {

	Change compare(T before, T after, MetaDataProvider provider);
}
