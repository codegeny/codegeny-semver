package org.codegeny.semver.checkers;

import java.lang.reflect.Method;
import java.util.Objects;

import org.codegeny.semver.Change;
import org.codegeny.semver.MethodChangeChecker;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class ChangeDefaultClauseChecker implements MethodChangeChecker {

	@Override
	public Change check(Method previous, Method current) {
		if (previous == null || current == null) {
			return NOT_APPLICABLE;
		}
		if (!previous.getDeclaringClass().isAnnotation() || !current.getDeclaringClass().isAnnotation()) {
			return NOT_APPLICABLE;
		}
		Object previousDefaultClause = previous.getDefaultValue();
		Object currentDefaultClause = current.getDefaultValue();
		if (previousDefaultClause != null && currentDefaultClause != null && !Objects.equals(previousDefaultClause, currentDefaultClause)) {
			return Change.MINOR;
		}
		return NOT_APPLICABLE;
	}
}
