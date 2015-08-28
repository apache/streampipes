import java.net.URI;
import java.util.Arrays;

import com.clarkparsia.empire.SupportsRdfId.URIKey;

import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.storm.sentiment.controller.SentimentDetectionController;


public class TestSentiment {

	public static void main(String[] args)
	{
		SepaDescription desc = new SentimentDetectionController().declareModel();
		desc.setRdfId(new URIKey(URI.create("http://test2")));
		SepaInvocation invoc = new SepaInvocation(desc);
		invoc.setRdfId(new URIKey(URI.create("http://test")));
		
		EventSchema schema = new EventSchema();
		EventPropertyPrimitive p1 = new EventPropertyPrimitive(XSD._string.toString(), "test", "", Arrays.asList(URI.create(SO.Text)));
		p1.setRdfId(new URIKey(URI.create("http://test3")));
		p1.setElementName(new URIKey(URI.create("http://test3")).toString());
		schema.addEventProperty(p1);
		
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(new KafkaTransportProtocol("localhost", 9092, "test.topic", "localhost", 2181));
		grounding.setTransportFormats(Arrays.asList(new TransportFormat(MessageFormat.Thrift)));
		
		EventStream stream = new EventStream();
		stream.setEventSchema(schema);
		stream.setEventGrounding(grounding);
		
		invoc.setInputStreams(Arrays.asList(stream));
		
		EventSchema schema2 = new EventSchema();
		EventPropertyPrimitive p2 = new EventPropertyPrimitive(XSD._string.toString(), "test", "", Arrays.asList(URI.create(SO.Text)));
		p2.setRdfId(new URIKey(URI.create("http://test3")));
		p2.setElementName(new URIKey(URI.create("http://test3")).toString());
		schema2.addEventProperty(p2);
		schema2.addEventProperty(new EventPropertyPrimitive(XSD._string.toString(), "sentiment", "", Arrays.asList(URI.create(SO.Text))));
		
		EventGrounding grounding2 = new EventGrounding();
		grounding2.setTransportProtocol(new KafkaTransportProtocol("localhost", 9092, "output.topic", "localhost", 2181));
		grounding2.setTransportFormats(Arrays.asList(new TransportFormat(MessageFormat.Thrift)));
		
		EventStream stream2 = new EventStream();
		stream2.setEventSchema(schema2);
		stream2.setEventGrounding(grounding2);
		
		invoc.setOutputStream(stream2);
		((MappingPropertyUnary)invoc.getStaticProperties().get(0)).setInternalName("sentimentMapsTo");
		((MappingPropertyUnary)invoc.getStaticProperties().get(0)).setMapsFrom(URI.create(p1.getElementName()));
		((MappingPropertyUnary)invoc.getStaticProperties().get(0)).setMapsTo(URI.create(p1.getElementName()));
		
		new SentimentDetectionController().invokeRuntime(invoc);
		
	}
}
