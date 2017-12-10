package org.streampipes.pe.sources.samples.random;

import org.streampipes.commons.Utils;
import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.quality.Accuracy;
import org.streampipes.model.quality.EventPropertyQualityDefinition;
import org.streampipes.model.quality.EventStreamQualityDefinition;
import org.streampipes.model.quality.Frequency;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.XSD;
import org.streampipes.pe.sources.samples.config.SampleSettings;
import org.streampipes.pe.sources.samples.config.SourcesConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public abstract class RandomNumberStream implements EventStreamDeclarer {
	
	SpKafkaProducer kafkaProducer;
	private String topic;
	
	final static long SIMULATION_DELAY_MS = SourcesConfig.INSTANCE.getSimulaitonDelayMs();
	final static int SIMULATION_DELAY_NS = SourcesConfig.INSTANCE.getSimulaitonDelayNs();
	
	public RandomNumberStream(String topic) {
		this.topic = topic;
	}
	
	protected SpDataStream prepareStream(String topic, String messageFormat) {
		SpDataStream stream = new SpDataStream();

		EventSchema schema = new EventSchema();

		List<EventPropertyQualityDefinition> randomValueQualities = new ArrayList<EventPropertyQualityDefinition>();
		randomValueQualities.add(new Accuracy((float) 0.5));

		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "timestamp", "",
				Utils.createURI("http://schema.org/DateTime")));
		eventProperties.add(new EventPropertyPrimitive(XSD._integer.toString(), "randomValue", "",
				Utils.createURI("http://schema.org/Number"), randomValueQualities));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "randomString", "",
				Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "count", "",
				Utils.createURI("http://schema.org/Number")));

		List<EventStreamQualityDefinition> eventStreamQualities = new ArrayList<EventStreamQualityDefinition>();
		eventStreamQualities.add(new Frequency(1));

		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(SampleSettings.kafkaProtocol(topic));
		grounding.setTransportFormats(
				Utils.createList(new TransportFormat(messageFormat)));

		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setHasEventStreamQualities(eventStreamQualities);

		return stream;
	}
	
	@Override
	public void executeStream() {

		kafkaProducer = new SpKafkaProducer(SourcesConfig.INSTANCE.getKafkaUrl(), topic);

		Runnable r = new Runnable() {

			@Override
			public void run() {
				Random random = new Random();
				int j = 0;
				for (int i = 0; i < SourcesConfig.INSTANCE.getMaxEvents(); i++) {
					try {
						if (j % 50 == 0) {
							System.out.println(j +" Events (Random Number) sent.");
						}
						Optional<byte[]> nextMsg = getMessage(System.currentTimeMillis(), random.nextInt(100), j);
						if (nextMsg.isPresent()) kafkaProducer.publish(nextMsg.get());
						Thread.sleep(SIMULATION_DELAY_MS, SIMULATION_DELAY_NS);
						if (j % SourcesConfig.INSTANCE.getSimulationWaitEvery() == 0) {
							Thread.sleep(SourcesConfig.INSTANCE.getSimulationWaitFor());
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
