package de.fzi.cep.sepa.runtime.flat.protocol.kafka;

import org.apache.commons.lang.RandomStringUtils;

import de.fzi.cep.sepa.commons.messaging.IMessageListener;
import de.fzi.cep.sepa.commons.messaging.kafka.KafkaConsumerGroup;
import de.fzi.cep.sepa.runtime.flat.protocol.Consumer;

public class KafkaConsumer extends Consumer implements IMessageListener {

	private String zookeeperHost;
	private int zookeeperPort;
	private String topic;
	
	private int counter = 0;
	
	private de.fzi.cep.sepa.commons.messaging.kafka.KafkaConsumerGroup kafkaConsumerGroup;
	
	public KafkaConsumer(String zookeeperHost, int zookeeperPort, String topic) {
		this.zookeeperHost = zookeeperHost;
		this.zookeeperPort = zookeeperPort;
		this.topic = topic;
	}
	
	@Override
	public void openConsumer() {
		kafkaConsumerGroup = new KafkaConsumerGroup(zookeeperHost +":" +zookeeperPort, RandomStringUtils.randomAlphabetic(6),
				new String[] {topic}, this);
		kafkaConsumerGroup.run(1);
	}

	@Override
	public void closeConsumer() {
		kafkaConsumerGroup.shutdown();
	}

	@Override
	public void onEvent(String json) {
		notify(json);	
		counter++;
		if (counter % 1000 == 0) System.out.println(counter + " Events sent.");
	}

}
