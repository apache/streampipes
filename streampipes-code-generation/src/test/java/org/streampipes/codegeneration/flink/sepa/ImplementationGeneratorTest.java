package org.streampipes.codegeneration.flink.sepa;

import static org.junit.Assert.*;

import org.junit.Test;

import org.streampipes.codegeneration.utils.TV;
import org.streampipes.codegeneration.utils.Utils;

public class ImplementationGeneratorTest {

	@Test
	public void testBuild() {
		String actual = new ImplementationGenerator(null, TV.NAME, TV.PACKAGE_NAME).build().toString();
		String expected = Utils.readResourceFile("expected_TestProject_java");
		assertEquals(expected, actual);
	}

}
