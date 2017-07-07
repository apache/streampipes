package org.streampipes.pe.proasense.monitoring.streams;

import com.google.gson.JsonObject;
import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.commons.Utils;
import org.streampipes.commons.config.ClientConfiguration;
import org.streampipes.messaging.EventListener;
import org.streampipes.messaging.kafka.StreamPipesKafkaConsumer;
import org.streampipes.messaging.kafka.StreamPipesKafkaProducer;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.vocabulary.MessageFormat;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.pe.proasense.config.ProaSenseSettings;
import eu.proasense.internal.PredictedEvent;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PredictionStream implements EventStreamDeclarer, Runnable, EventListener<byte[]> {

	private static final String IN_TOPIC = "eu.proasense.internal.oa.mhwirth.predicted";
	private static final String OUT_TOPIC = "eu.proasense.internal.sp.monitoring.prediction";
	
	
	private TDeserializer deserializer;
	private StreamPipesKafkaProducer producer;
	
	@Override
	public EventStream declareModel(SepDescription sep) {
		
		EventStream stream = new EventStream();
		stream.setName("Prediction Monitoring");
		stream.setDescription("Monitors output of ProaSense predictions");
		stream.setUri(sep.getUri() + "/prediction");


		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
			
		eventProperties.add(EpProperties.stringEp("time", SO.Text));
		eventProperties.add(EpProperties.stringEp("pdfType", SO.Text));
		eventProperties.add(EpProperties.stringEp("eventName", SO.Text));
		
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
		Thread thread = new Thread(new PredictionStream());
		thread.start();
	}
	
	@Override
	public void onEvent(byte[] payload) {
		System.out.println(payload);
		Optional<PredictedEvent> recEvent = deserialize(payload);
		if (recEvent.isPresent())
			producer.publish(toJson(recEvent.get()).getBytes());
	}

	private String toJson(PredictedEvent re) {
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("pdfType", re.getPdfType().getValue());
		jsonObj.addProperty("eventName", re.getEventName());
		jsonObj.addProperty("time", re.getTimestamp());
		System.out.println(jsonObj.toString());
		return jsonObj.toString();
		
	}

	private Optional<PredictedEvent> deserialize(byte[] bytes) {
		try {
			PredictedEvent recEvent = new PredictedEvent();
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

	@Override
	public boolean isExecutable() {
		return true;
	}

	public static void main(String[] args) {
		Thread thread = new Thread(new PredictionStream());
		thread.start();
	}
}
