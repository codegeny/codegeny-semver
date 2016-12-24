package org.codegeny.semver.checkers;

import java.lang.reflect.GenericDeclaration;

import org.codegeny.semver.Change;
import org.codegeny.semver.GenericDeclarationChangeChecker;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class DeleteTypeParameterChecker implements GenericDeclarationChangeChecker {

	@Override
	public Change check(GenericDeclaration previous, GenericDeclaration current) {
		if (previous == null || current == null) {
			return Change.PATCH;
		}
		if (current.getTypeParameters().length < previous.getTypeParameters().length) {
			return Change.MAJOR;
		}
		return Change.PATCH;
	}
}
