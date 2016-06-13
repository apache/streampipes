package de.fzi.cep.sepa.streampipes.codegeneration.flink;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.squareup.javapoet.ClassName;

import de.fzi.cep.sepa.streampipes.codegeneration.utils.TV;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.Utils;

public class InitGeneratorTest {

	@Test
	public void testBuild() {
		String actual = new InitGenerator(null, TV.NAME, TV.PACKAGE_NAME, true).build().toString();
		String expected = Utils.readResourceFile("expected_Init_java");

		assertEquals(expected, actual);
	}

}
