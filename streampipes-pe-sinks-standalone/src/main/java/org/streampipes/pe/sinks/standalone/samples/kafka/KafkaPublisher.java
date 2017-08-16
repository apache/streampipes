package org.streampipes.pe.sinks.standalone.samples.kafka;

import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.InternalEventProcessor;

public class KafkaPublisher implements InternalEventProcessor<byte[]> {

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
