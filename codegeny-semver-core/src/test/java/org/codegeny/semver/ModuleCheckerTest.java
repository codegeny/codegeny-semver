package org.codegeny.semver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Member;
import java.util.function.UnaryOperator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ModuleCheckerTest {
	
	static class ReplacingOutputStream extends FilterOutputStream {
		
		private final byte[] match;
		private final byte[] replace;
		private int matched;
		
		public ReplacingOutputStream(OutputStream out, byte[] match, byte[] replace) {
			super(out);
			this.match = match;
			this.replace = replace;
		}
		
		@Override
		public void write(int b) throws IOException {
			if (match[matched] == b) {
				matched++;
				if (matched == match.length) {
					for (int i = 0; i < replace.length; i++) {
						super.write(replace[i]);
					}
					matched = 0;
				}
			} else {
				for (int i = 0; i < matched; i++) {
					super.write(match[i]);
				}
				matched = 0;
				super.write(b);
			}
		}

		@Override
		public void close() throws IOException {
			for (int i = 0; i < matched; i++) {
				super.write(match[i]);
			}
			super.close();
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

	@Test
	public void test() throws Exception {
		
		File source1 = shade("classes1");
		File source2 = shade("classes2");
		File zipFile = temp.newFile("classes2.jar");
		
		try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipFile))) {
			zip(source2, source2, zip);
		}

		Assert.assertEquals(Change.MAJOR, ModuleChecker.newConfiguredInstance().check(new Module(source1), new Module(zipFile), new Reporter() {
			
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
		}));
	}
}
