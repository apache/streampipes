package de.fzi.cep.sepa.streampipes_flink_code_generation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

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

		ControllerBuilder cb = new ControllerBuilder(null, "", "");
		EventStream eventStream = StreamBuilder.createStream(TV.NAME, TV.DESCRIPTION, TV.PATH_NAME).schema(SchemaBuilder.create().build()).build();

		String actual = cb.getEventStream(eventStream, 0);
		String expected = "EventStream stream0 = StreamBuilder" + ".createStream(\"" + TV.NAME + "\", \"" + TV.DESCRIPTION + "\", \""
				+ TV.PATH_NAME + "\")\n" + ".schema(" + "SchemaBuilder.create()" + ".properties(eventProperties)"
				+ ".build()" + ").build();";

		assertEquals(expected, actual);
	}

	@Test
	public void testAppendGetOutputStrategyWithNoEventProperty() {
		ControllerBuilder cb = new ControllerBuilder(null, "", "");

		AppendOutputStrategy appendStrategy = new AppendOutputStrategy();

		String actual = cb.getAppendOutputStrategy(appendStrategy);
		String expected = "AppendOutputStrategy outputStrategy = new AppendOutputStrategy();\n";

		assertEquals(expected, actual);
	}

	@Test
	public void testAppendGetOutputStrategyWithEventProperties() {
		ControllerBuilder cb = new ControllerBuilder(null, "", "");

		AppendOutputStrategy appendStrategy = new AppendOutputStrategy();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "testTime", "",
				de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number")));

		String actual = cb.getAppendOutputStrategy(appendStrategy);
		String expected = "AppendOutputStrategy outputStrategy = new AppendOutputStrategy();\nList<EventProperty> appendProperties = new ArrayList<EventProperty>();\nappendProperties.add(new EventPropertyPrimitive(XSD._long.toString(),\n"
				+ "\"appendedTime\", \"\", de.fzi.cep.sepa.commons.Utils.createURI(\"http://schema.org/Number\")));";

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
		EventStream es = new EventStream();
		es.setEventSchema(new EventSchema());
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
