package de.fzi.cep.sepa.streampipes_flink_code_generation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;

import de.fzi.cep.sepa.model.builder.EpProperties;
import de.fzi.cep.sepa.model.builder.PrimitivePropertyBuilder;
import de.fzi.cep.sepa.model.builder.SchemaBuilder;
import de.fzi.cep.sepa.model.builder.StreamBuilder;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.vocabulary.XSD;

public class ControllerBuilderTest {

	@Test
	public void testGetEventStream() {

		Builder b = MethodSpec.methodBuilder("testMethod");
		ControllerBuilder cb = new ControllerBuilder(null, "", "");
		EventStream eventStream = StreamBuilder.createStream(TV.NAME, TV.DESCRIPTION, TV.PATH_NAME)
				.schema(SchemaBuilder.create().build()).build();

		Builder actual = cb.getEventStream(b, eventStream, 0);
		String expected = "void testMethod() {\n  java.util.List<java.util.ArrayList> eventProperties0 = new de.fzi.cep.sepa.model.impl.eventproperty.EventProperty<de.fzi.cep.sepa.model.impl.eventproperty.EventProperty>();\n"
				+ "  de.fzi.cep.sepa.model.impl.EventStream stream0 = de.fzi.cep.sepa.model.builder.StreamBuilder.createStream(\"TestProject\", \"Example description\", \"sepa/testProject\").schema(de.fzi.cep.sepa.model.builder.StreamBuilder.create().properties(eventProperties0).build()).build();\n}\n";


		assertEquals(expected, actual.build().toString());
	}

	
	@Test
	public void testGetEventProperties() {
		Builder b = MethodSpec.methodBuilder("testMethod");
		ControllerBuilder cb = new ControllerBuilder(null, "", "");
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(PrimitivePropertyBuilder.createPropertyRestriction("http://test.org#mytest").build());

		String actual = cb.getEventProperties(b, eventProperties, 0).build().toString();
		String expected = "void testMethod() {\n  java.util.List<java.util.ArrayList> eventProperties0 = new de.fzi.cep.sepa.model.impl.eventproperty.EventProperty<de.fzi.cep.sepa.model.impl.eventproperty.EventProperty>();\n"
				+ "  de.fzi.cep.sepa.model.impl.eventproperty.EventProperty e0 = de.fzi.cep.sepa.model.builder.PrimitivePropertyBuilder.createPropertyRestriction(\"http://test.org#mytest\").build();\n"
				+ "  eventProperties0.add(e0);\n}\n";

		assertEquals(expected, actual);
	}

	@Test
	public void testAppendGetOutputStrategyWithNoEventProperty() {
		ControllerBuilder cb = new ControllerBuilder(null, "", "");

		AppendOutputStrategy appendStrategy = new AppendOutputStrategy();

		String actual = cb.getAppendOutputStrategy(appendStrategy, 0);
		String expected = "AppendOutputStrategy outputStrategy0 = new AppendOutputStrategy();\nList<EventProperty> appendProperties = new ArrayList<EventProperty>();\n" + 
				"outputStrategy0.setEventProperties(appendProperties);";

		assertEquals(expected, actual);
	}

	@Test
	public void testAppendGetOutputStrategyWithEventProperties() {
		ControllerBuilder cb = new ControllerBuilder(null, "", "");

		AppendOutputStrategy appendStrategy = new AppendOutputStrategy();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "testTime", "",
				de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number")));
		appendStrategy.setEventProperties(eventProperties);

		String actual = cb.getAppendOutputStrategy(appendStrategy, 0);
		String expected = "AppendOutputStrategy outputStrategy0 = new AppendOutputStrategy();\n"
				+ "List<EventProperty> appendProperties = new ArrayList<EventProperty>();\n"
				+ "appendProperties.add(EpProperties.stringEp(\"testTime\", \"http://schema.org/Number\"));\n" 
				+ "outputStrategy0.setEventProperties(appendProperties);";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetOutputStrategies() {
		ControllerBuilder cb = new ControllerBuilder(null, "", "");

		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();

		String actual = cb.getOutputStrategies(strategies);
		String expected = "List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();\n";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetSupportedGrounding() {
		ControllerBuilder cb = new ControllerBuilder(null, "", "");

		String actual = cb.getSupportedGrounding(null);
		String expected = "desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());";

		assertEquals(expected, actual);
	}
	
	@Test
	public void testBuild() {
		SepaDescription sepa = new SepaDescription(TV.PATH_NAME, TV.NAME, TV.DESCRIPTION);
		// EventStream es = new EventStream(TV.PATH_NAME, TV.NAME,
		// TV.DESCRIPTION, new EventSchema());
		List<EventProperty> eventProperties0 = new ArrayList<EventProperty>();
		eventProperties0.add(PrimitivePropertyBuilder.createPropertyRestriction("http://test.org#mytest").build());
		EventStream es = StreamBuilder.createStream(TV.PATH_NAME, TV.NAME, TV.DESCRIPTION)
				.schema(SchemaBuilder.create().properties(eventProperties0).build()).build();
		sepa.addEventStream(es);

		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		AppendOutputStrategy outputStrategy = new AppendOutputStrategy();
		strategies.add(outputStrategy);
		sepa.setOutputStrategies(strategies);

		String actual = new ControllerBuilder(sepa, TV.NAME, TV.PACKAGE_NAME).build().toString();
		String expected = Utils.readResourceFile("expected_TestProjectController_java");

		assertEquals(expected, actual);
	}

}
