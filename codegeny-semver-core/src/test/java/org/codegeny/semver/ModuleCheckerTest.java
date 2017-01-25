package org.codegeny.semver;

import static org.codegeny.semver.ModuleChecker.newConfiguredInstance;
import static org.codegeny.semver.Reporter.noop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Member;
import java.util.function.UnaryOperator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ModuleCheckerTest {
		
	private static class ReplacingOutputStream extends FilterOutputStream {
		
		private int matched;
		private final byte[] replacement;
		private final byte[] target;
		
		public ReplacingOutputStream(OutputStream out, byte[] target, byte[] replacement) {
			super(out);
			this.target = target;
			this.replacement = replacement;
		}
		
		@Override
		public void close() throws IOException {
			for (int i = 0; i < matched; i++) {
				super.write(target[i]);
			}
			matched = 0; // just in case close() is called more than once
			super.close();
		}

		@Override
		public void write(int b) throws IOException {
			if (target[matched] == b) {
				matched++;
				if (matched == target.length) {
					for (int i = 0; i < replacement.length; i++) {
						super.write(replacement[i]);
					}
					matched = 0;
				}
			} else {
				for (int i = 0; i < matched; i++) {
					super.write(target[i]);
				}
				matched = 0;
				super.write(b);
			}
		}
	}
	
	@Rule
	public final TemporaryFolder temp = new TemporaryFolder();
	
	private void copy(File source, File target, UnaryOperator<OutputStream> decorator) throws IOException {
		if (source.isFile()) {
			byte[] bytes = new byte[1024];
			try (InputStream in = new FileInputStream(source); OutputStream out = decorator.apply(new FileOutputStream(target))) {
				for (int length; (length = in.read(bytes)) >= 0;) {
					out.write(bytes, 0, length);
				}
			}
		} else {
			target.mkdirs();
			for (File file : source.listFiles()) {
				copy(file, new File(target, file.getName()), decorator);
			}
		}
	}
	
	private File shade(String name) throws IOException {
		File root = new File(getClass().getResource(".").getFile());
		File source = new File(root, name);
		File target = temp.newFolder(name, "org", "codegeny", "semver", "classes_");
		copy(source, target, out -> new ReplacingOutputStream(out, name.getBytes(), "classes_".getBytes()));
		return new File(temp.getRoot(), name);
	}
	
	@Test
	public void test() throws Exception {
		
		File source1 = shade("classes1");
		File source2 = shade("classes2");
		File zipFile = temp.newFile("classes2.jar");
		
		try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipFile))) {
			zip(source2, source2, zip);
		}

		Assert.assertEquals(Change.MAJOR, ModuleChecker.newConfiguredInstance().check(new Module(source1), new Module(zipFile), Reporter.noop().and(new Reporter() {
			
			@Override
			public void report(Change change, String name, Member previous, Member current) {
				if (change != Change.PATCH) {
					System.out.printf("%s :: %s :: %s - %s%n", change, name, previous, current);
				}
			}
			
			@Override
			public void report(Change change, String name, Class<?> previous, Class<?> current) {
				if (change != Change.PATCH) {
					System.out.printf("%s :: %s :: %s - %s%n", change, name, previous, current);
				}
			}
		})));
	}
	
	@Test(expected = UncheckedIOException.class)
	public void testWithException() throws Exception {
		newConfiguredInstance().check(new Module(temp.newFile("empty1.jar")), new Module(temp.newFile("empty2.jar")), noop());
	}

	private void zip(File root, File source, ZipOutputStream zip) throws IOException {
		if (source.isFile()) {
			String name = root.toURI().relativize(source.toURI()).toString();
			zip.putNextEntry(new ZipEntry(name));
			byte[] bytes = new byte[1024];
			try (InputStream in = new FileInputStream(source)) {
				for (int length; (length = in.read(bytes)) >= 0;) {
					zip.write(bytes, 0, length);
				}
			}
			zip.closeEntry();
		} else {
			for (File file : source.listFiles()) {
				zip(root, file, zip);
			}
		}
	}
}
