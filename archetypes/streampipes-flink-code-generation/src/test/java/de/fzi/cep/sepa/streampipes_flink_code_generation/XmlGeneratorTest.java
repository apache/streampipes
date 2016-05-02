package de.fzi.cep.sepa.streampipes_flink_code_generation;

import static org.junit.Assert.*;

import org.junit.Test;

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
