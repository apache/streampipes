package de.fzi.cep.sepa.streampipes.codegeneration.flink.sec;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa.FlinkSepaControllerGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.TV;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.Utils;

public class FlinkSecControllerGeneratorTest {

	@Test
	public void testIsVisualizable() {
		SecDescription sec = TV.getSecDescription();
		FlinkSecControllerGenerator cd = new FlinkSecControllerGenerator(sec, TV.NAME, TV.PACKAGE_NAME);

		String actual = cd.isVisualizable().build().toString();
		String expected = "@java.lang.Override\n" +
			"public boolean isVisualizable() {\n" +
			"  return false;\n"
			+ "}\n";

		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetHtml() {
		SecDescription sec = TV.getSecDescription();
		FlinkSecControllerGenerator cd = new FlinkSecControllerGenerator(sec, TV.NAME, TV.PACKAGE_NAME);

		String actual = cd.getHtml().build().toString();
		String expected = "@java.lang.Override\n" +
			"public String getHtml(de.fzi.cep.sepa.model.impl.graph.SecInvocation graph) {\n" +
			"  return null;\n"
			+ "}\n";

		assertEquals(expected, actual);
	}

	@Test
	public void testBuild() {
		
		SecDescription sec = TV.getSecDescription();

		String actual = new FlinkSecControllerGenerator(sec, TV.NAME, TV.PACKAGE_NAME).build().toString();
		String expected = Utils.readResourceFile("expected_FlinkSecController_java");

		assertEquals(expected, actual);
	}

}
