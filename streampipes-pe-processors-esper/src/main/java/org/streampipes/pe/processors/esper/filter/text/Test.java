package org.streampipes.pe.processors.esper.filter.text;

public class Test {

	public static void main(String[] args) throws Exception {
	/*
		SEP sep = Transformer.fromJsonLd(SEP.class, HttpJsonParser.getContentFromUrl(new URI("http://localhost:8089/twitter/t")));
		
		SEPA textfilter = Transformer.fromJsonLd(SEPA.class, HttpJsonParser.getContentFromUrl(new URI("http://localhost:8090/sepa/textfilter")));
		

		graph.setName("Test TextFilter");
		graph.setDescription("Test");
		sep.getEventStreams().get(0).setName("TwitterEvent");
		List<EventStream> inputStreams = new ArrayList<EventStream>();
		inputStreams.add(sep.getEventStreams().get(0));
		//graph.addInputStream(sep.getEventStreams().get(0));
		graph.setInputStreams(inputStreams);
		EventStream outputStream = new EventStream();
		
		EventSchema originalSchema = sep.getEventStreams().get(0).getEventSchema();
		outputStream.setEventSchema(originalSchema);
		
		outputStream.setEventSchema(originalSchema);
		
		EventGrounding targetEventGrounding = new EventGrounding();
		targetEventGrounding.setPort(61616);
		targetEventGrounding.setUri("tcp://localhost");
		targetEventGrounding.setTopicName("FZI.SEPA.Test2");
		
		outputStream.setEventGrounding(targetEventGrounding);
		outputStream.setName("ObamaEvent");
		outputStream.setUri("http://test");
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		FreeTextStaticProperty epsg = new FreeTextStaticProperty("keyword", "Example");
		epsg.setValue("RT");
		staticProperties.add(epsg);
		OneOfStaticProperty xProp = new OneOfStaticProperty("operation", "operator");
		xProp.addOption(new Option("MATCHES"));
		xProp.addOption(new Option("CONTAINS", true));
		staticProperties.add(xProp);
		
		//MappingProperty yProp = new MappingProperty("text", "Select Text Property");
		//yProp.setMapsTo(SEPAUtils.getURIbyPropertyName(sep.getEventStreams().get(0), "text"));
		//staticProperties.add(yProp);
		
		graph.setStaticProperties(staticProperties);
		graph.setOutputStream(outputStream);
	
		new TextFilterController().invokeRuntime(graph);
	*/
	}
}
