package org.streampipes.codegeneration.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
