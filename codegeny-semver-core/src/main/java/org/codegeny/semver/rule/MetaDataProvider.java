package org.codegeny.semver.rule;

import java.util.Collection;

import org.codegeny.semver.model.XClass;
import org.codegeny.semver.model.XMethod;

public interface MetaDataProvider {
	
	class Composite implements MetaDataProvider {
		
		private final Collection<? extends MetaDataProvider> providers;

		public Composite(Collection<? extends MetaDataProvider> providers) {
			this.providers = providers;
		}

		@Override
		public boolean isPublic(XClass klass) {
			return this.providers.stream().anyMatch(p -> p.isPublic(klass));
		}

		@Override
		public boolean isPublic(XMethod method) {
			return this.providers.stream().anyMatch(p -> p.isPublic(method));
		}

		@Override
		public boolean isImplementedByClient(XClass klass) {
			return this.providers.stream().anyMatch(p -> p.isImplementedByClient(klass));
		}

		@Override
		public boolean isImplementedByClient(XMethod method) {
			return this.providers.stream().anyMatch(p -> p.isImplementedByClient(method));
		}
	}
	
	boolean isPublic(XClass klass);
	
	boolean isPublic(XMethod method);
	
	default boolean isImplementedByClient(XClass klass) {
		return true;
	}
	
	default boolean isImplementedByClient(XMethod method) {
		return true;
	}
}
