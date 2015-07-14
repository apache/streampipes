package de.fzi.cep.sepa.esper.filter.text;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.Option;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.runtime.EPRuntime;
import de.fzi.cep.sepa.runtime.param.CamelConfig;
import de.fzi.cep.sepa.runtime.param.DataType;
import de.fzi.cep.sepa.runtime.param.EndpointInfo;
import de.fzi.cep.sepa.runtime.param.EngineParameters;
import de.fzi.cep.sepa.runtime.param.RuntimeParameters;

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
