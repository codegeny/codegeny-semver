package org.codegeny.semver.checkers;

import java.lang.reflect.Member;

import org.codegeny.semver.Change;
import org.codegeny.semver.MemberChangeChecker;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class DecreaseAccessChecker  implements MemberChangeChecker {

	@Override
	public Change check(Member previous, Member current) {
		if (previous == null || current == null) {
			return NOT_APPLICABLE;
		}
		Access previousAccess = Access.from(previous);
		Access currentAccess = Access.from(current);
		if (Access.COMPARATOR.compare(currentAccess, previousAccess) < 0) {
			return Change.MAJOR;
		}
		return NOT_APPLICABLE;
	}
}