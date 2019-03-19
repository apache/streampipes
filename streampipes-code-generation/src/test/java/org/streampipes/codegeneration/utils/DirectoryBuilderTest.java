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

public class DirectoryBuilderTest {
  private static String root = System.getProperty("java.io.tmpdir") + "/StreampipesTest/";

//	@Test
//	public void testCreateDirectories() {
//		String[] dirs = { root + "test/", root + "test/test", root + "my/new/directory" };
//
//		assertTrue("Could not create the directory structure", DirectoryBuilder.createDirectories(dirs));
//
//		for (String dir : dirs) {
//			assertTrue(dir + " was not created", isDir(dir));
//		}
//	}
//
//	private boolean isDir(String dir) {
//		File f = new File(dir);
//		return f.exists() && f.isDirectory();
//	}
//
//	@Before
//	public void before() {
//		try {
//			FileUtils.deleteDirectory(new File(root));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	@After
//	public void after() {
//		try {
//			FileUtils.deleteDirectory(new File(root));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}
