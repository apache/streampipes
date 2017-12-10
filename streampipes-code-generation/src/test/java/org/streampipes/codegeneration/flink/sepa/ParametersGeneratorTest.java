package org.streampipes.codegeneration.flink.sepa;

import static org.junit.Assert.assertEquals;

import org.streampipes.model.schema.EventProperty;

public class ParametersGeneratorTest {
//
//	@Test
//	public void testBuild() {
//		SepaDescription desc = new SepaDescription(TV.PATH_NAME, TV.NAME, TV.DESCRIPTION);
//		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
//		EventProperty e1 = PrimitivePropertyBuilder.createPropertyRestriction("http://test.org#test1").build();
//		eventProperties.add(e1);
//
//		EventStream stream1 = StreamBuilder
//				.createStreamRestriction("localhost/" + TV.PATH_NAME)
//				.schema(
//						SchemaBuilder.create()
//							.properties(eventProperties)
//							.build()
//						).build();
//		desc.addEventStream(stream1);
//
//
//		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
//		staticProperties.add(new MappingPropertyUnary(URI.create(e1.getElementName()), "mappingFirst",
//				"Mapping First: ", ""));
//		staticProperties.add(new FreeTextStaticProperty("freeText", "Free Text: ", ""));
//
//		desc.setStaticProperties(staticProperties);
//		String actual = new ParametersGenerator(desc, TV.NAME, TV.PACKAGE_NAME).build().toString();
//		String expected = Utils.readResourceFile("expected_TestProjectParameters_java");
//		assertEquals(expected, actual);
//	}

}
