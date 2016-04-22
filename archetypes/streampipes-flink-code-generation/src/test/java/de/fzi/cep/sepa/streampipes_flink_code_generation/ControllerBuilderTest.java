package de.fzi.cep.sepa.streampipes_flink_code_generation;

import static org.junit.Assert.*;

import org.junit.Test;

import de.fzi.cep.sepa.model.impl.graph.SepaDescription;

public class ControllerBuilderTest {

	@Test
	public void testBuild() {
			SepaDescription sepa = new SepaDescription(TV.PATH_NAME, TV.NAME, TV.DESCRIPTION);
			String actual = new ControllerBuilder(sepa, TV.NAME, TV.PACKAGE_NAME).build().toString();
			String expected = Utils.readResourceFile("expected_TestProjectController_java");
			assertEquals(expected, actual);
	}

}
