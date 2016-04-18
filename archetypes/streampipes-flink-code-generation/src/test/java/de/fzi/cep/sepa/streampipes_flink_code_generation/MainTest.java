package de.fzi.cep.sepa.streampipes_flink_code_generation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.flink.hadoop.shaded.com.google.common.io.Files;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import junit.framework.TestCase;

/**
 * Unit test for simple App.
 */
public class MainTest extends TestCase {
	private String name = "TestProject";
	private String packeageName = "de.fzi.cep.sepa.flink.test.project";

	public void testCreateProgram() {
		String actual = Main.createProgram(name, packeageName).toString();
		String expected = Utils.readResourceFile("expected_TestProjectProgram_java");
		assertEquals(expected, actual);
	}

	public void testCreateController() {
		String actual = Main.createController(name, packeageName).toString();
		String expected = Utils.readResourceFile("expected_TestProjectController_java");
		assertEquals(expected, actual);
	}

	public void testCreateParametes() {
		String actual = Main.createParameters(name, packeageName).toString();
		String expected = Utils.readResourceFile("expected_TestProjectParameters_java");
		assertEquals(expected, actual);
	}

	public void testCreateImplementation() {
		String actual = Main.createImplementation(name, packeageName).toString();
		String expected = Utils.readResourceFile("expected_TestProject_java");
		assertEquals(expected, actual);
	}

	public void testCreatePom() {
		String actual = Main.createPomFile(name, packeageName);
		String expected = Utils.readResourceFile("pom_xml");

		assertEquals(expected, actual);
	}

}
