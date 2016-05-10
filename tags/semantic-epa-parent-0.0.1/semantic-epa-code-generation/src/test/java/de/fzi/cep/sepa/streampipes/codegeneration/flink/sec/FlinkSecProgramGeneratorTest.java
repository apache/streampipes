package de.fzi.cep.sepa.streampipes.codegeneration.flink.sec;

import static org.junit.Assert.*;

import org.junit.Test;

import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.TV;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.Utils;

public class FlinkSecProgramGeneratorTest {
	
	@Test
	public void testBuild() {
		
		SecDescription sec = TV.getSecDescription();

		String actual = new FlinkSecProgramGenerator(sec, TV.NAME, TV.PACKAGE_NAME).build().toString();
		String expected = Utils.readResourceFile("expected_FlinkSecProgram_java");

		assertEquals(expected, actual);
	}

}
