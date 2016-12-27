package org.codegeny.semver.checkers;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

import org.codegeny.semver.Change;
import org.codegeny.semver.MemberChangeChecker;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class ChangeStaticToNonStaticChecker implements MemberChangeChecker {

	@Override
	public Change check(Member previous, Member current) {
		if (previous == null || current == null) {
			return NOT_APPLICABLE;
		}
		if (Modifier.isStatic(previous.getModifiers()) ^ Modifier.isStatic(current.getModifiers())) {
			return Change.MAJOR;
		}
		return NOT_APPLICABLE;
	}
}
