package de.fzi.cep.sepa.streampipes_flink_code_generation;

import static org.junit.Assert.*;

import org.junit.Test;

public class ControllerBuilderTest {

	@Test
	public void testBuild() {
			String actual = new ControllerBuilder(TV.NAME, TV.PACKAGE_NAME).build().toString();
			String expected = Utils.readResourceFile("expected_TestProjectController_java");
			assertEquals(expected, actual);
	}

}
