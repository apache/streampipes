package de.fzi.proasense.monitoring.streams;

import com.google.gson.JsonObject;
import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.messaging.EventListener;
import de.fzi.cep.sepa.messaging.kafka.StreamPipesKafkaConsumer;
import de.fzi.cep.sepa.messaging.kafka.StreamPipesKafkaProducer;
import de.fzi.cep.sepa.sdk.stream.EpProperties;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.proasense.config.ProaSenseSettings;
import eu.proasense.internal.RecommendationEvent;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DecisionMakingStream implements EventStreamDeclarer, EventListener<byte[]>, Runnable {

	private static final String IN_TOPIC = "eu.proasense.internal.pandda.mhwirth.recommendation";
	private static final String OUT_TOPIC = "eu.proasense.internal.sp.monitoring.recommendation";
	
	private StreamPipesKafkaProducer producer;
	private TDeserializer deserializer;
	
	@Override
	public EventStream declareModel(SepDescription sep) {
		
		EventStream stream = new EventStream();
		stream.setName("Recommendation Monitoring");
		stream.setDescription("Monitors output of ProaSense recommendations");
		stream.setUri(sep.getUri() + "/recommendation");

		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
			
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "time", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "action_timestamp", "", Utils.createURI(SO.Number)));
		eventProperties.add(EpProperties.stringEp("action", SO.Text));
		eventProperties.add(EpProperties.stringEp("actor", SO.Text));
		eventProperties.add(EpProperties.stringEp("recommendationId", SO.Text));
		
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(OUT_TOPIC));
		grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));
				
		stream.setEventGrounding(grounding);	
		
		return stream;
	}

	@Override
	public void executeStream() {
		System.out.println("****");
		System.out.println("Executing Decision Making Stream...");
		Thread thread = new Thread(new DecisionMakingStream());
		thread.start();
	}

	@Override
	public boolean isExecutable() {
		return true;
	}

	@Override
	public void onEvent(byte[] payload) {
		Optional<RecommendationEvent> recEvent = deserialize(payload);
		if (recEvent.isPresent())
			producer.publish(toJson(recEvent.get()).getBytes());
	}

	private String toJson(RecommendationEvent re) {
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("action", re.getAction());
		jsonObj.addProperty("actor", re.getActor());
		jsonObj.addProperty("recommendationId", re.getRecommendationId());
		jsonObj.addProperty("time", re.getTimestamp());
		jsonObj.addProperty("actionTimestamp", Long.parseLong(re.getEventProperties().get("action_timestamp").getValue()));
		System.out.println(jsonObj.toString());
		return jsonObj.toString();
		
	}

	private Optional<RecommendationEvent> deserialize(byte[] bytes) {
		try {
			RecommendationEvent recEvent = new RecommendationEvent();
			deserializer.deserialize(recEvent, bytes);
			return Optional.of(recEvent);
		} catch (TException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

	@Override
	public void run() {
		producer = new StreamPipesKafkaProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), OUT_TOPIC);
		deserializer = new TDeserializer(new TBinaryProtocol.Factory());
		StreamPipesKafkaConsumer kafkaConsumerGroup = new StreamPipesKafkaConsumer(ClientConfiguration.INSTANCE.getKafkaUrl(),
				IN_TOPIC, this);
		Thread thread = new Thread(kafkaConsumerGroup);
		thread.start();
	}
	
	public static void main(String[] args) {
		Thread thread = new Thread(new DecisionMakingStream());
		thread.start();
	}

}
