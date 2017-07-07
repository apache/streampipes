package de.fzi.cep.sepa.sources.samples.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.messaging.kafka.StreamPipesKafkaProducer;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.quality.Accuracy;
import de.fzi.cep.sepa.model.impl.quality.EventPropertyQualityDefinition;
import de.fzi.cep.sepa.model.impl.quality.EventStreamQualityDefinition;
import de.fzi.cep.sepa.model.impl.quality.Frequency;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.sources.samples.config.SampleSettings;

public abstract class RandomNumberStream implements EventStreamDeclarer {
	
	StreamPipesKafkaProducer kafkaProducer;
	private String topic;
	
	final static long SIMULATION_DELAY_MS = ClientConfiguration.INSTANCE.getSimulationDelayMs();
	final static int SIMULATION_DELAY_NS = ClientConfiguration.INSTANCE.getSimulationDelayNs();
	
	public RandomNumberStream(String topic) {
		this.topic = topic;
	}
	
	protected EventStream prepareStream(String topic, String messageFormat) {
		EventStream stream = new EventStream();

		EventSchema schema = new EventSchema();

		List<EventPropertyQualityDefinition> randomValueQualities = new ArrayList<EventPropertyQualityDefinition>();
		randomValueQualities.add(new Accuracy((float) 0.5));

		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "timestamp", "",
				de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/DateTime")));
		eventProperties.add(new EventPropertyPrimitive(XSD._integer.toString(), "randomValue", "",
				de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number"), randomValueQualities));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "randomString", "",
				de.fzi.cep.sepa.commons.Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "count", "",
				de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number")));

		List<EventStreamQualityDefinition> eventStreamQualities = new ArrayList<EventStreamQualityDefinition>();
		eventStreamQualities.add(new Frequency(1));

		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(SampleSettings.kafkaProtocol(topic));
		grounding.setTransportFormats(
				de.fzi.cep.sepa.commons.Utils.createList(new TransportFormat(messageFormat)));

		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setHasEventStreamQualities(eventStreamQualities);

		return stream;
	}
	
	@Override
	public void executeStream() {

		kafkaProducer = new StreamPipesKafkaProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), topic);

		Runnable r = new Runnable() {

			@Override
			public void run() {
				Random random = new Random();
				int j = 0;
				for (int i = 0; i < ClientConfiguration.INSTANCE.getSimulationMaxEvents(); i++) {
					try {
						if (j % 10000 == 0) {
							System.out.println(j +" Events (Random Number) sent.");
						}
						Optional<byte[]> nextMsg = getMessage(System.currentTimeMillis(), random.nextInt(100), j);
						if (nextMsg.isPresent()) kafkaProducer.publish(nextMsg.get());
						Thread.sleep(SIMULATION_DELAY_MS, SIMULATION_DELAY_NS);
						if (j % ClientConfiguration.INSTANCE.getWaitEvery() == 0) {
							Thread.sleep(ClientConfiguration.INSTANCE.getWaitForMs());
						}
						j++;
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
			}
		};
		Thread thread = new Thread(r);
		thread.start();

	}

	@Override
	public boolean isExecutable() {
		return true;
	}
	
	protected String randomString() {
		String[] randomStrings = new String[] { "a", "b", "c", "d" };
		Random random = new Random();
		return randomStrings[random.nextInt(3)];
	}
	
	protected abstract Optional<byte[]> getMessage(long nanoTime, int randomNumber, int counter);
}
