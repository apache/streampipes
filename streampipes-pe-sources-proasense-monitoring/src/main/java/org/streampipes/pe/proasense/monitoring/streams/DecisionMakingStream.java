package org.streampipes.pe.proasense.monitoring.streams;

import com.google.gson.JsonObject;
import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.commons.Utils;
import org.streampipes.commons.config.ClientConfiguration;
import org.streampipes.messaging.EventConsumer;
import org.streampipes.messaging.kafka.SpKafkaConsumer;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.vocabulary.MessageFormat;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.pe.proasense.config.ProaSenseSettings;
import eu.proasense.internal.RecommendationEvent;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DecisionMakingStream implements EventStreamDeclarer, EventConsumer<byte[]>, Runnable {

	private static final String IN_TOPIC = "eu.proasense.internal.pandda.mhwirth.recommendation";
	private static final String OUT_TOPIC = "eu.proasense.internal.sp.monitoring.recommendation";
	
	private SpKafkaProducer producer;
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
		producer = new SpKafkaProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), OUT_TOPIC);
		deserializer = new TDeserializer(new TBinaryProtocol.Factory());
		SpKafkaConsumer kafkaConsumerGroup = new SpKafkaConsumer(ClientConfiguration.INSTANCE.getKafkaUrl(),
				IN_TOPIC, this);
		Thread thread = new Thread(kafkaConsumerGroup);
		thread.start();
	}
	
	public static void main(String[] args) {
		Thread thread = new Thread(new DecisionMakingStream());
		thread.start();
	}

}
