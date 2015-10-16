package de.fzi.cep.sepa.actions.samples.kafka;

import de.fzi.cep.sepa.commons.messaging.IMessageListener;
import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;

public class KafkaPublisher implements IMessageListener {

	private ProaSenseInternalProducer producer;
	
	public KafkaPublisher(ProaSenseInternalProducer producer)
	{
		this.producer = producer;
	}
	
	@Override
	public void onEvent(String message) {
		producer.send(message.getBytes());
	}

}
