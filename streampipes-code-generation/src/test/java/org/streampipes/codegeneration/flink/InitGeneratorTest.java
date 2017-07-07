package org.streampipes.codegeneration.flink;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.streampipes.codegeneration.utils.TV;
import org.streampipes.codegeneration.utils.Utils;

public class InitGeneratorTest {

	@Test
	public void testBuild() {
		String actual = new InitGenerator(null, TV.NAME, TV.PACKAGE_NAME, true).build().toString();
		String expected = Utils.readResourceFile("expected_Init_java");

		assertEquals(expected, actual);
	}

}
