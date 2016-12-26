package org.codegeny.semver;

import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Module {
	
	private static Pattern PATTERN = Pattern.compile("^([a-zA-Z$_][a-zA-Z0-9$_]*\\/)*[a-zA-Z$_][a-zA-Z0-9$_]*\\.class$");
	
	private final Set<File> dependencies = new HashSet<>();
	private final File main;
	
	public Module(File main, File... dependencies) {
		this.main = main;
		this.dependencies.addAll(Arrays.asList(dependencies));
	}

	private Stream<String> accept(File file) {
		if (file.isFile()) {
			return Stream.of(file).map(this::getRelativeFileName).filter(this::isClassFileName);
		} else if (file.isDirectory()) {
			return Stream.of(file.listFiles()).flatMap(this::accept);
		} else {
			throw new RuntimeException();
		}
	}

	public Set<File> getArchives() {
		return Stream.concat(Stream.of(main), dependencies.stream()).collect(toSet());
	}
	
	public Set<String> getClassNames() {
		if (main.isFile()) { // jar
			try (JarFile jarFile = new JarFile(main)) {
				return jarFile.stream().map(JarEntry::getName).filter(this::isClassFileName).map(this::toClassName).collect(toSet());
			} catch (IOException ioException) {
				throw new UncheckedIOException(ioException);
			}
		} else if (main.isDirectory()) {
			return accept(main).map(this::toClassName).collect(toSet());
		} else {
			throw new RuntimeException();
		}
	}
	
	private String getRelativeFileName(File file) {
		return file.getAbsolutePath().substring(main.getAbsolutePath().length());
	}
	
	private boolean isClassFileName(String fileName) {
		return PATTERN.matcher(fileName).matches();
	}
	
	private String toClassName(String classFileName) {
		return classFileName.substring(0, classFileName.length() - ".class".length()).replace('/', '.');
	}
}
