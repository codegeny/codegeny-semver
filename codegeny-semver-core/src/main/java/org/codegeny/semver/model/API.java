package org.codegeny.semver.model;

import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassReader;

public class API {
	
	private final Map<String, XClass> classes = new HashMap<>();
	private final APIClassPath classPath;
	private final Map<String, XPackage> packages = new HashMap<>();
	
	public API(APIClassPath classPath) {
		this.classPath = classPath;
	}
	
	void addClass(XClass klass) {
		this.classes.put(klass.getName(), klass);
	}
	
	void addPackage(XPackage pakkage) {
		this.packages.put(pakkage.getName(), pakkage);
	}
	
	public XClass getClass(String name) {
		if (name == null) {
			return null;
		}
		name = name.replace('/', '.');
		if (!this.classes.containsKey(name)) {
			readClass(name);
		}
		return this.classes.get(name);
	}
	
	public Set<XClass> getClasses() {
		return classes.values().stream().collect(toSet());
	}
	
	public XPackage getPackage(String name) {
		if (name == null) {
			return null;
		}
		name = name.replace('/', '.');
		if (!this.packages.containsKey(name)) {
			readPackage(name);
		}
		return this.packages.get(name);
	}
	
	public Set<XPackage> getPackages() {
		return packages.values().stream().collect(toSet());
	}
	
	private void readClass(String name) {
		try (InputStream inputStream = classPath.loadResource(name.replace('.', '/').concat(".class"))) {
			if (inputStream != null) {
				new ClassReader(inputStream).accept(new APIClassVisitor(this), ClassReader.SKIP_CODE);
			} else {
				System.out.println("not found " + name);
				classes.put(name, null);
			}
		} catch (IOException ioException) {
			throw new UncheckedIOException(name, ioException);
		}
	}
	
	private void readPackage(String name) {
		XPackage pakkage = new XPackage(name);
		packages.put(name, pakkage);
		try (InputStream inputStream = classPath.loadResource(name.replace('.', '/').concat("/package-info.class"))) {
			if (inputStream != null) {
				new ClassReader(inputStream).accept(new APIPackageVisitor(this, pakkage), ClassReader.SKIP_CODE);
			}
		} catch (IOException ioException) {
			throw new UncheckedIOException(name, ioException);
		}
	}
}
