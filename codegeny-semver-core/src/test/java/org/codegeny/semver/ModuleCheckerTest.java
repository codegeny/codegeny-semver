package org.codegeny.semver;

import static org.codegeny.semver.Change.PATCH;
import static org.codegeny.semver.ModuleChecker.newConfiguredInstance;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ModuleCheckerTest {

	@Rule
	public final TemporaryFolder temp = new TemporaryFolder();

	@Test
	public void test() throws IOException {
		File folder = temp.newFolder("library");
		File jar = temp.newFile("library.jar");

		createArchives(jar, folder);

		assertEquals(PATCH, newConfiguredInstance().check(new Module(jar), new Module(folder), Reporter.noop()));
	}

	private void createArchives(File jar, File folder) throws IOException {
		
		String fileName = DummyClass.class.getName().replace(".", "/").concat(".class");
		File file = new File(folder, fileName);
		File pakkage = file.getParentFile();
		pakkage.mkdirs();

		try (
				ZipOutputStream jarOutput = new ZipOutputStream(new FileOutputStream(jar));
				OutputStream fileOutput = new FileOutputStream(file);
				InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {

			jarOutput.putNextEntry(new ZipEntry(fileName));

			byte[] bytes = new byte[1024];
			for (int length; (length = input.read(bytes)) >= 0;) {
				jarOutput.write(bytes, 0, length);
				fileOutput.write(bytes, 0, length);
			}

			jarOutput.closeEntry();
		}
	}
}
