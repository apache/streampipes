package de.fzi.cep.sepa.esper.filter.text;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.openrdf.model.Graph;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.JSONLDMode;
import org.openrdf.rio.helpers.JSONLDSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import de.fzi.cep.sepa.esper.util.StringOperator;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.MappingProperty;
import de.fzi.cep.sepa.model.impl.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.Option;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.model.util.SEPAUtils;
import de.fzi.cep.sepa.rest.http.HttpJsonParser;
import de.fzi.cep.sepa.rest.util.Utils;
import de.fzi.cep.sepa.runtime.EPRuntime;
import de.fzi.cep.sepa.runtime.param.CamelConfig;
import de.fzi.cep.sepa.runtime.param.DataType;
import de.fzi.cep.sepa.runtime.param.EndpointInfo;
import de.fzi.cep.sepa.runtime.param.EngineParameters;
import de.fzi.cep.sepa.runtime.param.OutputStrategy;
import de.fzi.cep.sepa.runtime.param.RuntimeParameters;
import de.fzi.cep.sepa.storage.util.Transformer;

public class Test {

	public static void main(String[] args) throws Exception {
	
		SEP sep = Transformer.fromJsonLd(SEP.class, HttpJsonParser.getContentFromUrl("http://localhost:8089/twitter/t"));
		
		SEPA textfilter = Transformer.fromJsonLd(SEPA.class, HttpJsonParser.getContentFromUrl("http://localhost:8090/sepa/textfilter"));
		
		SEPAInvocationGraph graph = new SEPAInvocationGraph(textfilter);
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
		
		MappingProperty yProp = new MappingProperty("text", "Select Text Property");
		yProp.setMapsTo(SEPAUtils.getURIbyPropertyName(sep.getEventStreams().get(0), "text"));
		staticProperties.add(yProp);
		
		graph.setStaticProperties(staticProperties);
		graph.setOutputStream(outputStream);
	
		new TextFilterController().invokeRuntime(graph);
	
	}
}
