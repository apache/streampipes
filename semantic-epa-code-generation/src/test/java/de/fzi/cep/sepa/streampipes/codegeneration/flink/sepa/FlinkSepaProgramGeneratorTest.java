package de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa;

import static org.junit.Assert.*;

import org.junit.Test;

import de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa.FlinkSepaProgramGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.TV;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.Utils;

public class FlinkSepaProgramGeneratorTest {

	@Test
	public void testBuild() {
		String actual = new FlinkSepaProgramGenerator(null, TV.NAME, TV.PACKAGE_NAME).build().toString();
		String expected = Utils.readResourceFile("expected_FlinkSepaProgram_java");
		assertEquals(expected, actual);
	}

}
