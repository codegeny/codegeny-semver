package org.codegeny.semver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ModuleCheckerTest {

	@Rule
	public final TemporaryFolder temp = new TemporaryFolder();

	@Test
	public void test() throws IOException {
		
		String name = DummyClass.class.getName().replace(".", "/").concat(".class");
		
		File folder = temp.newFolder("library");
		File jar = temp.newFile("library.jar");
		File file = new File(folder, name);
		file.getParentFile().mkdirs();
		
		try (
				JarOutputStream jarOutput = new JarOutputStream(new FileOutputStream(jar));
				OutputStream fileOutput = new FileOutputStream(file);
				InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(name)) {
			
			ZipEntry zipEntry = new ZipEntry(name);
			jarOutput.putNextEntry(zipEntry);
			
			byte[] bytes = new byte[1024];
			for (int length; (length = input.read(bytes)) >= 0;) {
				jarOutput.write(bytes, 0, length);
				fileOutput.write(bytes, 0, length);
			}
			
			jarOutput.closeEntry();
		}
				
		ModuleChecker.newConfiguredInstance().check(new Module(jar), new Module(folder),  new Reporter() {
			
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
