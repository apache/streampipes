package de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa;

import static org.junit.Assert.*;

import org.junit.Test;

import de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa.ProgramGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.TV;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.Utils;

public class ProgramGeneratorTest {

	@Test
	public void testBuild() {
		String actual = new ProgramGenerator(null, TV.NAME, TV.PACKAGE_NAME).build().toString();
		String expected = Utils.readResourceFile("expected_TestProjectProgram_java");
		assertEquals(expected, actual);
	}

}
