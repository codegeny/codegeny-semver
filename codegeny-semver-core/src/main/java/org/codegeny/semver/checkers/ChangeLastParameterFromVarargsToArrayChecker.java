package org.codegeny.semver.checkers;

import java.lang.reflect.Executable;

import org.codegeny.semver.Change;
import org.codegeny.semver.ExecutableChangeChecker;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class ChangeLastParameterFromVarargsToArrayChecker implements ExecutableChangeChecker {

	@Override
	public Change check(Executable previous, Executable current) {
		if (previous == null || current == null) {
			return Change.PATCH;
		}
		if (previous.isVarArgs() && !current.isVarArgs()) {
			return Change.MAJOR;
		}
		return Change.PATCH;
	}
	
	
}
