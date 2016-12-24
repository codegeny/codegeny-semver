package org.codegeny.semver.checkers;

import org.codegeny.semver.ClassChangeChecker;
import org.kohsuke.MetaInfServices;

@MetaInfServices(ClassChangeChecker.class)
public class ClassChangeNonPublicToPublicChecker extends AbstractChangeNonPublicToPublicChecker<Class<?>> implements ClassChangeChecker {
	
	@Override
	protected int getModifiers(Class<?> target) {
		return target.getModifiers();
	}
}