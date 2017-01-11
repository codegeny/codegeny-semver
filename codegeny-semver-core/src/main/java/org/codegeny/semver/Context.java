package org.codegeny.semver;

public class Context {

	private final Compatibility compatibilityMode;
	private final Logger logger;
	private final Metadata metadata;

	public Context(Metadata metadata, Logger logger, Compatibility compatibilityMode) {
		this.metadata = metadata;
		this.logger = logger;
		this.compatibilityMode = compatibilityMode;
	}

	public Compatibility getCompatibilityMode() {
		return compatibilityMode;
	}

	public Logger getLogger() {
		return logger;
	}

	public Metadata getMetadata() {
		return metadata;
	}
}
