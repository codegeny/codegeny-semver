package org.codegeny.semver.checkers;

import static org.codegeny.semver.Change.MAJOR;
import static org.codegeny.semver.Change.MINOR;
import static org.codegeny.semver.checkers.Checkers.compareTypes;
import static org.codegeny.semver.checkers.Checkers.notNull;

import java.lang.reflect.GenericDeclaration;
import java.util.HashMap;

import org.codegeny.semver.Change;
import org.codegeny.semver.Checker;
import org.codegeny.semver.Metadata;

public enum GenericDeclarationCheckers implements Checker<GenericDeclaration> {
	
	ADD_TYPE_PARAMETER_WHEN_NO_PARAMETERS_EXIST {
		
		@Override
		public Change check(GenericDeclaration previous, GenericDeclaration current, Metadata metadata) {
			return MINOR.when(notNull(previous, current) && current.getTypeParameters().length > previous.getTypeParameters().length && previous.getTypeParameters().length == 0);
		}
	},
	ADD_TYPE_PARAMETER_WHEN_OTHER_PARAMETERS_EXIST {
		
		@Override
		public Change check(GenericDeclaration previous, GenericDeclaration current, Metadata metadata) {
			return MINOR.when(notNull(previous, current) && current.getTypeParameters().length > previous.getTypeParameters().length && previous.getTypeParameters().length > 0);
		}
	},
	CHANGE_TYPE_PARAMETERS_BOUNDS {
		
		@Override
		public Change check(GenericDeclaration previous, GenericDeclaration current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && current.getTypeParameters().length == previous.getTypeParameters().length && !compareTypes(previous.getTypeParameters(), current.getTypeParameters(), new HashMap<>()));
		}
	},
	DELETE_TYPE_PARAMETER {
		
		@Override
		public Change check(GenericDeclaration previous, GenericDeclaration current, Metadata metadata) {
			return MAJOR.when(notNull(previous, current) && current.getTypeParameters().length < previous.getTypeParameters().length);
		}
	}
}
