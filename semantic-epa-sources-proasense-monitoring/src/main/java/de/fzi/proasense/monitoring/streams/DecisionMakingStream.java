package de.fzi.proasense.monitoring.streams;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;

import com.google.gson.JsonObject;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.commons.messaging.IMessageListener;
import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;
import de.fzi.cep.sepa.commons.messaging.kafka.KafkaConsumerGroup;
import de.fzi.cep.sepa.desc.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.model.builder.EpProperties;
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

public class DecisionMakingStream implements EventStreamDeclarer, IMessageListener, Runnable {

	private static final String IN_TOPIC = "eu.proasense.internal.pandda.mhwirth.recommendation";
	private static final String OUT_TOPIC = "eu.proasense.internal.sp.monitoring.recommendation";
	
	private ProaSenseInternalProducer producer;
	private TDeserializer deserializer;
	
	@Override
	public EventStream declareModel(SepDescription sep) {
		
		EventStream stream = new EventStream();

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
		Thread thread = new Thread(new DecisionMakingStream());
		thread.start();
	}

	@Override
	public boolean isExecutable() {
		return true;
	}

	@Override
	public void onEvent(String json) {
		Optional<RecommendationEvent> recEvent = deserialize(json.getBytes());
		if (recEvent.isPresent())
			producer.send(toJson(recEvent.get()).getBytes());
	}

	private String toJson(RecommendationEvent re) {
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("action", re.getAction());
		jsonObj.addProperty("actor", re.getActor());
		jsonObj.addProperty("recommendationId", re.getRecommendationId());
		jsonObj.addProperty("time", re.getTimestamp());
		jsonObj.addProperty("actionTimestamp", Long.parseLong(re.getEventProperties().get("action_timestamp").getValue()));
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
		producer = new ProaSenseInternalProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), OUT_TOPIC);
		deserializer = new TDeserializer(new TBinaryProtocol.Factory());
		KafkaConsumerGroup kafkaConsumerGroup = new KafkaConsumerGroup(ClientConfiguration.INSTANCE.getZookeeperUrl(), "rec_mon",
				new String[] {IN_TOPIC}, this);
		kafkaConsumerGroup.run(1);
	}

}
