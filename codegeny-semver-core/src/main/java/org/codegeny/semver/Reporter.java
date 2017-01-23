package org.codegeny.semver;

import java.lang.reflect.Member;

public interface Reporter {

	void report(Change change, String name, Class<?> previous, Class<?> current);

	 <M extends Member> void report(Change change, String name, M previous, M current);

	
	static Reporter noop() {
		return new Reporter() {

			@Override
			public void report(Change change, String name, Class<?> previous, Class<?> current) {}

			@Override
			public <M extends Member> void report(Change change, String name, M previous, M current) {}
		};
	}
}
