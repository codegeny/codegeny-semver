package org.codegeny.semver;

import java.lang.reflect.Member;

public interface Reporter {

	void report(Change change, String name, Class<?> previous, Class<?> current);

	void report(Change change, String name, Member previous, Member current);
	
	static Reporter noop() {
		return new Reporter() {

			@Override
			public void report(Change change, String name, Class<?> previous, Class<?> current) {}

			@Override
			public void report(Change change, String name, Member previous, Member current) {}
		};
	}
}
