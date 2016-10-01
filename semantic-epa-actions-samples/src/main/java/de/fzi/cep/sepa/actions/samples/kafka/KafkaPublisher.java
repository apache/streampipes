package de.fzi.cep.sepa.actions.samples.kafka;

import de.fzi.cep.sepa.messaging.EventListener;
import de.fzi.cep.sepa.messaging.EventProducer;

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
