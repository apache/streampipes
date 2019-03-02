/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.codegeneration.utils;

import java.io.File;

public class UtilsTest {
	private static File temp;
//
//	@BeforeClass
//	public static void beforeClass() {
//		temp = Files.createTempDir();
//	}
//
//	@AfterClass
//	public static void afterClass() {
//		try {
//			FileUtils.deleteDirectory(temp);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void testWriteToFileJavaFile() {
//		JavaFile jf = JavaFile.builder("test", TypeSpec.classBuilder("Test").build()).build();
//		Utils.writeToFile(jf, temp.toString());
//
//		File f = new File(temp.getAbsolutePath() + "/test/Test.java");
//		assertTrue("File Test.java wasn't created", f.exists());
//		assertFalse("Test.jave is a directory but should be a file", f.isDirectory());
//
//		String actual = Utils.readFile(f.getAbsolutePath());
//		assertEquals("package test;\n\nclass Test {\n}\n", actual);
//
//	}
//
//	@Test
//	public void testWriteToFileString() {
//		String expected = "Thist is a test file!";
//		File file = new File(temp.getAbsolutePath() + "/testFile1");
//
//		Utils.writeToFile(expected, file.getAbsolutePath());
//
//		String actual = Utils.readFile(file.getAbsolutePath());
//
//		assertEquals(expected, actual.trim());
//	}
//
//	@Test
//	public void testToCamelCaseNoSpecialCharacter() {
//		String expected = "testCamelCase";
//		String actual = Utils.toCamelCase("testCamelCase");
//
//		assertEquals(expected, actual);
//	}
//
//	@Test
//	public void testToCamelCaseWithSpecialCharacter() {
//		String expected = "testCamelCase";
//		String actual = Utils.toCamelCase("test-camel-case");
//
//		assertEquals(expected, actual);
//	}
}
