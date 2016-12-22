package org.codegeny.semver.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public interface APIClassPath {
	
	static APIClassPath folder(File folder) {
		return resource -> {
			File f = new File(folder, resource);
			return f.exists() ? new FileInputStream(f) : null;
		};
	}
	
	static APIClassPath jar(JarFile jar) {
		return resource -> {
			JarEntry e = jar.getJarEntry(resource);
			return e == null ? null : jar.getInputStream(e);
		};
	}
	
	static APIClassPath classLoader(ClassLoader classLoader) {
		return classLoader::getResourceAsStream;
	}
	
	InputStream loadResource(String resource) throws IOException;
	
	default APIClassPath or(APIClassPath classPath) {
		return resource -> {
			InputStream result = loadResource(resource);
			return result != null ? result : classPath.loadResource(resource);
		};
	}
}
