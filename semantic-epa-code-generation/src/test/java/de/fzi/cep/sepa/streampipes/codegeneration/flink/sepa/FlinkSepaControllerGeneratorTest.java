package de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.fzi.cep.sepa.model.builder.PrimitivePropertyBuilder;
import de.fzi.cep.sepa.model.builder.SchemaBuilder;
import de.fzi.cep.sepa.model.builder.StreamBuilder;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa.FlinkSepaControllerGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.TV;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.Utils;

public class FlinkSepaControllerGeneratorTest {


	@Test
	public void testGetRuntime() {
		SepaDescription sepa = TV.getSepa();
		FlinkSepaControllerGenerator cd = new FlinkSepaControllerGenerator(sepa, TV.NAME, TV.PACKAGE_NAME);

		String actual = cd.getRuntime().build().toString();
		String expected = "@java.lang.Override\n"
				+ "protected de.fzi.cep.sepa.flink.FlinkSepaRuntime<de.fzi.cep.sepa.flink.test.project.TestProjectParameters> getRuntime(de.fzi.cep.sepa.model.impl.graph.SepaInvocation graph) {\n"
				+ "  String mappingFirst = de.fzi.cep.sepa.model.util.SepaUtils.getMappingPropertyName(graph, \"mappingFirst\");\n" + 
				"  String freeText = de.fzi.cep.sepa.model.util.SepaUtils.getFreeTextStaticPropertyValue(graph, \"freeText\");\n"
				+ "  de.fzi.cep.sepa.flink.test.project.TestProjectParameters staticParam = new de.fzi.cep.sepa.flink.test.project.TestProjectParameters(graph, mappingFirst, freeText);\n"
				+ "  return new " + TV.PACKAGE_NAME +"."+ TV.NAME
				+ "Program(staticParam, new de.fzi.cep.sepa.flink.FlinkDeploymentConfig(" + TV.PACKAGE_NAME
				+ ".Config.JAR_FILE, " + TV.PACKAGE_NAME + ".Config.FLINK_HOST, " + TV.PACKAGE_NAME
				+ ".Config.FLINK_PORT));\n"
				+ "}\n";


		assertEquals(expected, actual);
	}

	@Test
	public void testBuild() {
		SepaDescription sepa = new SepaDescription(TV.PATH_NAME, TV.NAME, TV.DESCRIPTION);
		List<EventProperty> eventProperties0 = new ArrayList<EventProperty>();
		eventProperties0.add(PrimitivePropertyBuilder.createPropertyRestriction("http://test.org#mytest").build());
		EventStream es = StreamBuilder.createStream(TV.PATH_NAME, TV.NAME, TV.DESCRIPTION)
				.schema(SchemaBuilder.create().properties(eventProperties0).build()).build();
		sepa.addEventStream(es);

		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		AppendOutputStrategy outputStrategy = new AppendOutputStrategy();
		strategies.add(outputStrategy);
		sepa.setOutputStrategies(strategies);

		// TODO remove after I got the answer from Dominik what the difference between uri and pathName is
		sepa.setUri(TV.PATH_NAME);

		String actual = new FlinkSepaControllerGenerator(sepa, TV.NAME, TV.PACKAGE_NAME).build().toString();
		String expected = Utils.readResourceFile("expected_FlinkSepaController_java");

		assertEquals(expected, actual);
	}



}
