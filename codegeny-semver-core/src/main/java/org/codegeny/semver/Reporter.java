package org.codegeny.semver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface Reporter {

	void report(Change change, String name, Class<?> previous, Class<?> current);

	void report(Change change, String name, Constructor<?> previous, Constructor<?> current);

	void report(Change change, String name, Field previous, Field current);

	void report(Change change, String name, Method previous, Method current);
}
