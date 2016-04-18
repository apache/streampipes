package de.fzi.cep.sepa.streampipes_flink_code_generation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

public class UtilsTest {
	private static File temp;

	@BeforeClass
	public static void beforeClass() {
		temp = Files.createTempDir();
	}

	@AfterClass
	public static void afterClass() {
		try {
			FileUtils.deleteDirectory(temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testWriteToFileJavaFile() {
		JavaFile jf = JavaFile.builder("test", TypeSpec.classBuilder("Test").build()).build();
		Utils.writeToFile(jf, temp.toString());

		File f = new File(temp.getAbsolutePath() + "/test/Test.java");
		assertTrue("File Test.java wasn't created", f.exists());
		assertFalse("Test.jave is a directory but should be a file", f.isDirectory());

		String actual = Utils.readFile(f.getAbsolutePath());
		assertEquals("package test;\n\nclass Test {\n}\n", actual);

	}

	@Test
	public void testWriteToFileString() {
		String expected = "Thist is a test file!";
		File file = new File(temp.getAbsolutePath() + "/testFile1");

		Utils.writeToFile(expected, file.getAbsolutePath());

		String actual = Utils.readFile(file.getAbsolutePath());
		
		assertEquals(expected, actual.trim());
	}
}
