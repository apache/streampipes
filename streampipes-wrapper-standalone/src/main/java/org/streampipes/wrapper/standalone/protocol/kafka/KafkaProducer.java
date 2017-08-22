package org.streampipes.wrapper.standalone.protocol.kafka;

import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.kafka.StreamPipesKafkaProducer;
import org.streampipes.wrapper.standalone.datatype.DatatypeDefinition;
import org.streampipes.wrapper.standalone.protocol.Producer;

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
