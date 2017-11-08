package org.streampipes.codegeneration.utils;

/**
 * Test Variables (TV)
 * 
 * @author philipp
 *
 */
public abstract class TV {
	public static String NAME = "TestProject";
	public static String PATH_NAME = "sepa/testProject";
	public static String DESCRIPTION = "Example description";
	public static String PACKAGE_NAME = "de.fzi.cep.sepa.flink.test.project";

//	public static SepaDescription getSepa() {
//		SepaDescription sepa = new SepaDescription(TV.PATH_NAME, TV.NAME, TV.DESCRIPTION);
//
//		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
//		EventProperty e1 = PrimitivePropertyBuilder.createPropertyRestriction("http://test.org#test1").build();
//		eventProperties.add(e1);
//
//		EventStream stream1 = StreamBuilder.createStreamRestriction("localhost/sepa/testproject")
//				.schema(SchemaBuilder.create().properties(eventProperties).build()).build();
//		sepa.addEventStream(stream1);
//
//		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
//		AppendOutputStrategy outputStrategy = new AppendOutputStrategy();
//		List<EventProperty> appendProperties = new ArrayList<EventProperty>();
//		appendProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "appendedTime", "",
//				org.streampipes.commons.Utils.createURI("http://schema.org/Number")));
//		outputStrategy.setEventProperties(appendProperties);
//		strategies.add(outputStrategy);
//		sepa.setOutputStrategies(strategies);
//
//		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
//		staticProperties
//				.add(new MappingPropertyUnary(URI.create(e1.getElementName()), "mappingFirst", "Mapping First: ", ""));
//		staticProperties.add(new FreeTextStaticProperty("freeText", "Free Text: ", ""));
//
//		sepa.setStaticProperties(staticProperties);
//
//		return sepa;
//	}
//
//	public static SecDescription getSecDescription() {
//		SecDescription sec = new SecDescription(TV.PATH_NAME, TV.NAME, TV.DESCRIPTION);
//		sec.setUri(TV.PATH_NAME);
//		EventStream stream1 = new EventStream();
//		EventSchema schema1 = new EventSchema();
//
//		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
////		EventProperty e1 = EpRequirements.numberReq();
////		eventProperties.add(e1);
//		schema1.setEventProperties(eventProperties);
//		stream1.setEventSchema(schema1);
//
//		stream1.setUri("localhost:8080/" + TV.PATH_NAME);
//		sec.addEventStream(stream1);
//
//		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
//		// staticProperties.add(new FreeTextStaticProperty("min", "min value",
//		// ""));
//		// staticProperties.add(new FreeTextStaticProperty("max", "max value",
//		// ""));
//		// staticProperties.add(new FreeTextStaticProperty("color", "Color of
//		// the cirlce", ""));
//
//		// staticProperties.add(new
//		// MappingPropertyUnary(URI.create(e1.getElementId()), "mapping",
//		// "Select Mapping", ""));
//
//		sec.setStaticProperties(staticProperties);
//		EventGrounding grounding = new EventGrounding();
//		grounding.setTransportFormats(Arrays.asList(new TransportFormat(MessageFormat.Json)));
//		grounding.setTransportProtocols(Arrays.asList(new JmsTransportProtocol()));
//		sec.setSupportedGrounding(grounding);
//
//		return sec;
//	}
}
