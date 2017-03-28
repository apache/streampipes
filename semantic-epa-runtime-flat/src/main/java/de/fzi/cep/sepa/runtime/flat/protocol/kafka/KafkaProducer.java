package de.fzi.cep.sepa.runtime.flat.protocol.kafka;

import de.fzi.cep.sepa.messaging.EventProducer;
import de.fzi.cep.sepa.messaging.kafka.StreamPipesKafkaProducer;
import de.fzi.cep.sepa.runtime.flat.datatype.DatatypeDefinition;
import de.fzi.cep.sepa.runtime.flat.protocol.Producer;

import java.io.Serializable;

public class KafkaProducer extends Producer implements Serializable {

	private String kafkaHostname;
	private int kafkaPort;
	private String topic;
	
	private EventProducer producer;
	
	public KafkaProducer(String kafkaHostname, int kafkaPort, String topic, DatatypeDefinition dataType) {
		super(dataType);
		this.kafkaHostname = kafkaHostname;
		this.kafkaPort = kafkaPort;
		this.topic = topic;
	}
	
	@Override
	public void publish(Object message) {
		producer.publish(dataType.marshal(message));
	}

	@Override
	public void openProducer() {
		producer = new StreamPipesKafkaProducer(kafkaHostname +":" +kafkaPort, topic);
	}

	@Override
	public void closeProducer() {
		producer.closeProducer();
	}

}
