package org.codegeny.semver.checkers;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.codegeny.semver.Change;
import org.codegeny.semver.Metadata;
import org.codegeny.semver.MetadataAware;
import org.codegeny.semver.MethodChangeChecker;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class AddMethorChecker implements MethodChangeChecker, MetadataAware {

	private Metadata metadata;
	
	@Override
	public Change check(Method previous, Method current) {
		if (previous != null || current == null) {
			return NOT_APPLICABLE;
		}
		if (metadata.isImplementedByClient(current) && !current.isDefault() && !Modifier.isFinal(current.getModifiers())) {
			return Change.MAJOR;
		}
		return Change.MINOR;
	}

	@Override
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
}
