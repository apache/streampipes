package de.fzi.cep.sepa.streampipes.codegeneration.flink;

import static org.junit.Assert.*;

import org.junit.Test;

import de.fzi.cep.sepa.streampipes.codegeneration.flink.XmlGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.TV;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.Utils;

public class XmlGeneratorTest {

	@Test
	public void testGetPom() {
		String actual = new XmlGenerator(TV.NAME, "").getPomFile();
		String expected = Utils.readResourceFile("expected_pom_xml");

		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetWebXmlFile() {
		String actual = new XmlGenerator("", TV.PACKAGE_NAME).getWebXmlFile();
		String expected = Utils.readResourceFile("expected_web_xml");

		assertEquals(expected, actual);

	}
}
