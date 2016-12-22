package org.codegeny.semver.model;

public class XTypeArgument {

	private XType bound;
	private final XWildcard wildcard;

	public XTypeArgument(XWildcard wildcard, XType bound) {
		this.wildcard = wildcard;
		this.bound = bound;
	}
	
	public XTypeArgument() {
		this(XWildcard.EXACT, null);
	}

	public XType getBound() {
		return this.bound;
	}

	public XWildcard getWildcard() {
		return this.wildcard;
	}

	void setBound(XType bound) {
		this.bound = bound;
	}

	@Override
	public String toString() {
		if (bound == null) {
			return "?";
		}
		switch (wildcard) {
		case EXACT:
			return bound.toString();
		case EXTENDS:
			return "? extends ".concat(bound.toString());
		case SUPER:
			return "? super ".concat(bound.toString());
		default:
			throw new IllegalStateException("Unknown wildcard " + wildcard);
		}
	}
}
