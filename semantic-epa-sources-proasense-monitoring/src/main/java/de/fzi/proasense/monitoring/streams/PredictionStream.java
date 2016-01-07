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
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.proasense.config.ProaSenseSettings;
import eu.proasense.internal.PredictedEvent;

public class PredictionStream implements EventStreamDeclarer, Runnable, IMessageListener {

	private static final String IN_TOPIC = "eu.proasense.internal.oa.mhwirth.predicted";
	private static final String OUT_TOPIC = "eu.proasense.internal.sp.monitoring.prediction";
	
	
	private TDeserializer deserializer;
	private ProaSenseInternalProducer producer;
	
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
	public void onEvent(String json) {
		System.out.println(json);
		Optional<PredictedEvent> recEvent = deserialize(json.getBytes());
		if (recEvent.isPresent())
			producer.send(toJson(recEvent.get()).getBytes());
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
		producer = new ProaSenseInternalProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), OUT_TOPIC);
		deserializer = new TDeserializer(new TBinaryProtocol.Factory());
		KafkaConsumerGroup kafkaConsumerGroup = new KafkaConsumerGroup(ClientConfiguration.INSTANCE.getZookeeperUrl(), "pred_mon",
				new String[] {IN_TOPIC}, this);
		kafkaConsumerGroup.run(1);
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
