package de.fzi.cep.sepa.runtime.flat.protocol.kafka;

import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;
import de.fzi.cep.sepa.runtime.flat.datatype.DatatypeDefinition;
import de.fzi.cep.sepa.runtime.flat.protocol.Producer;

public class KafkaProducer extends Producer {

	private String kafkaHostname;
	private int kafkaPort;
	private String topic;
	
	private ProaSenseInternalProducer producer;
	
	public KafkaProducer(String kafkaHostname, int kafkaPort, String topic, DatatypeDefinition dataType) {
		super(dataType);
		this.kafkaHostname = kafkaHostname;
		this.kafkaPort = kafkaPort;
		this.topic = topic;
	}
	
	@Override
	public void publish(Object message) {
		producer.send(dataType.marshal(message));		
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
