package org.codegeny.semver.checkers;

import java.lang.reflect.Member;

import org.codegeny.semver.MemberChangeChecker;
import org.kohsuke.MetaInfServices;

@MetaInfServices(MemberChangeChecker.class)
public class MemberChangeNonPublicToPublicChecker extends AbstractChangeNonPublicToPublicChecker<Member> implements MemberChangeChecker {
	
	@Override
	protected int getModifiers(Member target) {
		return target.getModifiers();
	}
}