package de.fzi.cep.sepa.runtime.flat.protocol.kafka;

import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;
import de.fzi.cep.sepa.runtime.flat.protocol.Producer;

public class KafkaProducer extends Producer {

	private String kafkaHostname;
	private int kafkaPort;
	private String topic;
	
	private ProaSenseInternalProducer producer;
	
	public KafkaProducer(String kafkaHostname, int kafkaPort, String topic) {
		this.kafkaHostname = kafkaHostname;
		this.kafkaPort = kafkaPort;
		this.topic = topic;
	}
	
	@Override
	public void onEvent(String message) {
		producer.send(message.getBytes());
	}

	@Override
	public void openProducer() {
		producer = new ProaSenseInternalProducer(kafkaHostname +":" +kafkaPort, topic);
	}

	@Override
	public void closeProducer() {
		producer.shutdown();
	}

}
