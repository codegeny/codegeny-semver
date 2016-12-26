package org.codegeny.semver.checkers;

import java.lang.reflect.Method;

import org.codegeny.semver.Change;
import org.codegeny.semver.MethodChangeChecker;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class RemoveAnnotationElement implements MethodChangeChecker {

	@Override
	public Change check(Method previous, Method current) {
		if (previous == null || current != null) {
			return NOT_APPLICABLE;
		}
		if (!previous.getDeclaringClass().isAnnotation()) {
			return NOT_APPLICABLE;
		}
		return Change.MAJOR;
	}
}
