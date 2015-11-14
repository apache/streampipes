package test;
import java.net.URI;
import java.util.Arrays;
import java.util.Random;

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

	private static String KAFKA_HOST = "ipe-koi04.fzi.de";
	private static int KAFKA_PORT = 9092;

	private static String ZOOKEEPER_HOST = "ipe-koi04.fzi.de";
	private static int ZOOKEEPER_PORT = 2181;
	
	private static String MESSAGE_FORMAT = MessageFormat.Json;
	
	private static String LEFT_TOPIC = "SEPA.SEP.Twitter.Sample";
	private static String RIGHT_TOPIC = "output.topic";

	private static String SENTIMENT_MAPS_TO_FIELD = "content";
	private static String SENTIMENT_FIELD = "sentiment";
	

	public static void main(String[] args)
	{

		new SentimentDetectionController().invokeRuntime(getInvocationGraph());
		//new SentimentDetectionController().detachRuntime(sepa.getPipelineId());
		
	}
	
	public static SepaInvocation getInvocationGraph() {
		SepaDescription desc = new SentimentDetectionController().declareModel();
		desc.setRdfId(getId("sepaDescription"));

		SepaInvocation invoc = new SepaInvocation(desc);
		invoc.setRdfId(getId("seapInvocation"));
		
		//Schema left
		EventSchema schemaLeft = new EventSchema();
		EventPropertyPrimitive p1 = new EventPropertyPrimitive(XSD._string.toString(), SENTIMENT_MAPS_TO_FIELD, "", Arrays.asList(URI.create(SO.Text)));
		EventPropertyPrimitive p2 = new EventPropertyPrimitive(XSD._long.toString(), "timestamp", "", Arrays.asList(URI.create(SO.Text)));
		EventPropertyPrimitive p3 = new EventPropertyPrimitive(XSD._string.toString(), "userName", "", Arrays.asList(URI.create(SO.Text)));

//		p1.setRdfId(getId("EventPropertyPrimitive1"));
//		p1.setElementName(getId("EventPropertyPrimitive1").toString());
		schemaLeft.addEventProperty(p1);
		schemaLeft.addEventProperty(p2);
		schemaLeft.addEventProperty(p3);
		
		KafkaTransportProtocol kafkaLeft = new KafkaTransportProtocol(KAFKA_HOST, KAFKA_PORT, LEFT_TOPIC, ZOOKEEPER_HOST, ZOOKEEPER_PORT);

		EventStream stream = getStream(kafkaLeft, schemaLeft);
		
		invoc.setInputStreams(Arrays.asList(stream));
		
		//Schema right
		EventSchema schemaRight = new EventSchema();
		EventPropertyPrimitive p4 =new EventPropertyPrimitive(XSD._string.toString(), SENTIMENT_FIELD, "", Arrays.asList(URI.create(SO.Text)));
		schemaRight.addEventProperty(p4);
		schemaRight.addEventProperty(new EventPropertyPrimitive(XSD._string.toString(), SENTIMENT_MAPS_TO_FIELD, "", Arrays.asList(URI.create(SO.Text))));
		schemaRight.addEventProperty(new EventPropertyPrimitive(XSD._long.toString(), "timestamp", "", Arrays.asList(URI.create(SO.Text))));
		schemaRight.addEventProperty(new EventPropertyPrimitive(XSD._string.toString(), "userName", "", Arrays.asList(URI.create(SO.Text))));


		KafkaTransportProtocol kafkaRight = new KafkaTransportProtocol(KAFKA_HOST, KAFKA_PORT, RIGHT_TOPIC, ZOOKEEPER_HOST, ZOOKEEPER_PORT);


		EventStream stream2 = getStream(kafkaRight, schemaRight);
		
		invoc.setOutputStream(stream2);
		((MappingPropertyUnary)invoc.getStaticProperties().get(0)).setInternalName("sentimentMapsTo");
		((MappingPropertyUnary)invoc.getStaticProperties().get(0)).setMapsFrom(URI.create(p1.getElementName()));
		((MappingPropertyUnary)invoc.getStaticProperties().get(0)).setMapsTo(URI.create(p4.getElementName()));
		
		return invoc;
	}

	private static EventStream getStream(KafkaTransportProtocol kafka, EventSchema schema) {
		EventGrounding grounding = new EventGrounding();
		grounding.setRdfId(getRandomId("grounding"));
		kafka.setRdfId(getRandomId("kafka"));

		grounding.setTransportProtocol(kafka);
		TransportFormat tf = new TransportFormat(MESSAGE_FORMAT);
		tf.setRdfId(getRandomId("TransportFormat"));
		grounding.setTransportFormats(Arrays.asList(tf)); 
		EventStream stream = new EventStream();
		stream.setEventSchema(schema);
		stream.setEventGrounding(grounding);
		stream.setRdfId(getRandomId("EventStream"));

		return stream;
	}

	private static URIKey getId(String s) {
		return new URIKey(URI.create("http://" + s));
	}
	
	private static URIKey getRandomId(String s) {
		Random random = new Random();
		return getId(s + random.nextInt(100));
	}
}
