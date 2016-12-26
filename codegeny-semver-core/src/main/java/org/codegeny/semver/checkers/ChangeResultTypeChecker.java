package org.codegeny.semver.checkers;

import java.lang.reflect.Method;

import org.codegeny.semver.Change;
import org.codegeny.semver.MethodChangeChecker;

public class ChangeResultTypeChecker implements MethodChangeChecker {

	@Override
	public Change check(Method previous, Method current) {
		if (previous == null || current == null) {
			return NOT_APPLICABLE;
		}
		
		String previousResultType = previous.getReturnType().getName();
		String currentResultType = current.getReturnType().getName();
		
		if (!previousResultType.equals(currentResultType)) {
			return Change.MAJOR;
		}
		
		return NOT_APPLICABLE;
	}
}
