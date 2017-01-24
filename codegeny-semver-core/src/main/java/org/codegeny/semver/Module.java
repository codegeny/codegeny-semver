package org.codegeny.semver;

import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Module {
	
	private static final Pattern CLASS_PATTERN = Pattern.compile("^([a-zA-Z$_][a-zA-Z0-9$_]*\\/)*[a-zA-Z$_][a-zA-Z0-9$_]*\\.class$");
	
	private final Set<File> dependencies = new HashSet<>();
	private final File main;
	
	public Module(File main, File... dependencies) {
		this.main = main;
		this.dependencies.addAll(Arrays.asList(dependencies));
	}

	private Stream<File> accept(File file) {
		return file.isFile() ? Stream.of(file) : Stream.of(file.listFiles()).flatMap(this::accept);
	}

	public Set<String> getClassNames() throws IOException {
		return getResourceNames().stream()
			.filter(f -> CLASS_PATTERN.matcher(f).matches())
			.map(f -> f.replace(".class", "").replace('/', '.'))
			.collect(toSet());
	}
	
	public Set<File> getClassPath() {
		return Stream.concat(Stream.of(main), dependencies.stream()).collect(toSet());
	}
	
	public Set<String> getResourceNames() throws IOException {
		if (main.isFile()) {
			try (JarFile jar = new JarFile(main)) {
				return jar.stream().map(JarEntry::getName).collect(toSet());
			}
		} else {
			return accept(main).map(f -> main.toPath().relativize(f.toPath()).toString()).collect(toSet());
		}
	}
}
