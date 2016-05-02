package de.fzi.cep.sepa.streampipes_flink_code_generation;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Unit test for simple App.
 */
public class MainTest extends TestCase {

	@Test
	public void testCreateProgram() {
		String actual = Main.createProgram(TV.NAME, TV.PACKAGE_NAME).toString();
		String expected = Utils.readResourceFile("expected_TestProjectProgram_java");
		assertEquals(expected, actual);
	}


	@Test
	public void testCreateParametes() {
		String actual = Main.createParameters(TV.NAME, TV.PACKAGE_NAME).toString();
		String expected = Utils.readResourceFile("expected_TestProjectParameters_java");
		assertEquals(expected, actual);
	}

	@Test
	public void testCreateImplementation() {
		String actual = Main.createImplementation(TV.NAME, TV.PACKAGE_NAME).toString();
		String expected = Utils.readResourceFile("expected_TestProject_java");
		assertEquals(expected, actual);
	}



}
