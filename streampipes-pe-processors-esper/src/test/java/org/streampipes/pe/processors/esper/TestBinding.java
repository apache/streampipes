package org.streampipes.pe.processors.esper;

public class TestBinding {

//	public static void main(String[] args)
//	{
//		SepDescription sep = null;
//		try {
//			sep = StorageManager.INSTANCE.getStorageAPI().getSEPById("http://localhost:8089/random");
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		EventRateController eventRateController = new EventRateController();
//		SepaDescription test = eventRateController.declareModel();
//		test.setUri("http://test");
//		//test.setElementId("http://test");
//		test.setRdfId(new URIKey(URI.create("http://test.de")));
//
//		SepaInvocation testGraph = new SepaInvocation(test);
//		testGraph.setInputStreams(Utils.createList(sep.getEventStreams().get(0)));
//		EventStream outputStream = new EventStream();
//
//		List<EventProperty> outputProperties = new ArrayList<>();
//		outputProperties.add(new EventPropertyPrimitive(XSD._double.toString(),
//				"rate", "", Utils.createURI("http://schema.org/Number")));
//
//		EventSchema outputSchema = new EventSchema();
//		outputSchema.setEventProperties(outputProperties);
//		outputStream.setEventSchema(outputSchema);
//
//		EventGrounding outputGrounding = new EventGrounding();
//		outputGrounding.setTransportProtocol(new JmsTransportProtocol(ClientConfiguration.INSTANCE.getJmsHost(), ClientConfiguration.INSTANCE.getJmsPort(), "FZI.Test"));
//		outputStream.setEventGrounding(outputGrounding);
//
//		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
//
//		FreeTextStaticProperty text1 = new FreeTextStaticProperty("rate", "average/sec", "");
//		text1.setValue("5");
//		staticProperties.add(text1);
//		FreeTextStaticProperty text2 = new FreeTextStaticProperty("output", "output every (seconds)", "");
//		text2.setValue("10");
//		staticProperties.add(text2);
//
//		testGraph.setStaticProperties(staticProperties);
//		testGraph.setOutputStream(outputStream);
//
//		eventRateController.invokeRuntime(testGraph);
//	}
}
