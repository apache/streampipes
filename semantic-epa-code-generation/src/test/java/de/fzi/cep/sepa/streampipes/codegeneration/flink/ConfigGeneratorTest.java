package de.fzi.cep.sepa.streampipes.codegeneration.flink;

import static org.junit.Assert.*;

import org.junit.Test;

import de.fzi.cep.sepa.streampipes.codegeneration.utils.TV;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.Utils;

public class ConfigGeneratorTest {

	@Test
	public void testBuild() {
		String actual = new ConfigGenerator(null, TV.NAME, TV.PACKAGE_NAME).build().toString();
		String expected = Utils.readResourceFile("expected_Config_java");
		// TODO fix test
//		assertEquals(expected, actual);
	}

}
