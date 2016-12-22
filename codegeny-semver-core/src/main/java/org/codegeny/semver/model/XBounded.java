package org.codegeny.semver.model;

import java.util.Set;

public interface XBounded {
	
	XType getClassBound();
	
	Set<? extends XType> getInterfaceBounds();
}
