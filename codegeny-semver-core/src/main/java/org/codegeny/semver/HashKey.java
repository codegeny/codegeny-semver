package org.codegeny.semver;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Stream;

public final class HashKey implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private final Object[] elements;

	public HashKey(Object... elements) {
		this.elements = elements.clone();
	}
	
	public HashKey(Stream<?> stream) {
		this.elements = stream.toArray(i -> new Object[i]);
	}
	
	@Override
	public boolean equals(Object that) {
		return super.equals(that) || that instanceof HashKey && Arrays.deepEquals(this.elements, ((HashKey) that).elements);
	}

	@Override
	public int hashCode() {
		return Arrays.deepHashCode(this.elements);
	}
	
	@Override
	public String toString() {
		return Arrays.toString(this.elements);
	}
} 