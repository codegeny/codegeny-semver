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
	
	default Reporter and(Reporter that) {
		return new Reporter() {
			
			@Override
			public void report(Change change, String name, Class<?> previous, Class<?> current) {
				Reporter.this.report(change, name, previous, current);
				that.report(change, name, previous, current);
			}

			@Override
			public void report(Change change, String name, Member previous, Member current) {
				Reporter.this.report(change, name, previous, current);
				that.report(change, name, previous, current);
			}
		};
	}
}
