package org.codegeny.semver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ModuleCheckerTest {

	@Rule
	public final TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void test() throws IOException {
		
		System.out.println(folder.getRoot());
		
		File file = folder.newFile("library.jar");
		String name = DummyClass.class.getName().replace(".", "/").concat(".class");
		
		try (JarOutputStream output = new JarOutputStream(new FileOutputStream(file)); InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(name)) {
			ZipEntry zipEntry = new ZipEntry(name);
			output.putNextEntry(zipEntry);
			
			byte[] bytes = new byte[1024];
			for (int length; (length = input.read(bytes)) >= 0;) {
				output.write(bytes, 0, length);
			}
			
			output.closeEntry();
		}
		
		File file2 = folder.newFile("library2.jar");
		try (OutputStream output = new FileOutputStream(file2)) {
			Files.copy(file.toPath(), output);
		}
		
		ModuleChecker.newConfiguredInstance().check(new Module(file), new Module(file2),  new Reporter() {
			
			@Override
			public void report(Change change, String name, Method previous, Method current) {
				System.out.printf("%s :: %s :: %s - %s%n", change, name, previous, current);
			}
			
			@Override
			public void report(Change change, String name, Field previous, Field current) {
				System.out.printf("%s :: %s :: %s - %s%n", change, name, previous, current);
			}
			
			@Override
			public void report(Change change, String name, Constructor<?> previous, Constructor<?> current) {
				System.out.printf("%s :: %s :: %s - %s%n", change, name, previous, current);
			}
			
			@Override
			public void report(Change change, String name, Class<?> previous, Class<?> current) {
				System.out.printf("%s :: %s :: %s - %s%n", change, name, previous, current);
			}
		});
	}
}
