package org.streampipes.pe.sinks.standalone.samples.kafka;

import org.streampipes.messaging.EventListener;
import org.streampipes.messaging.EventProducer;

public class KafkaPublisher implements EventListener<byte[]> {

	private EventProducer producer;
	
	public KafkaPublisher(EventProducer producer)
	{
		this.producer = producer;
	}
	
	@Override
	public void onEvent(byte[] message) {
		producer.publish(message);
	}

}
