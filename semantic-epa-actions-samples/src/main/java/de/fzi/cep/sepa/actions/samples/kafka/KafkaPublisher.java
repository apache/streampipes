package de.fzi.cep.sepa.actions.samples.kafka;

import de.fzi.cep.sepa.commons.messaging.IMessageListener;
import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;

public class KafkaPublisher implements IMessageListener<byte[]> {

	private ProaSenseInternalProducer producer;
	
	public KafkaPublisher(ProaSenseInternalProducer producer)
	{
		this.producer = producer;
	}
	
	@Override
	public void onEvent(byte[] message) {
		producer.send(message);
	}

}
