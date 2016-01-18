package de.fzi.cep.sepa.runtime.flat.protocol.kafka;

import org.apache.commons.lang.RandomStringUtils;

import de.fzi.cep.sepa.commons.messaging.kafka.KafkaConsumerGroup;
import de.fzi.cep.sepa.runtime.flat.datatype.DatatypeDefinition;
import de.fzi.cep.sepa.runtime.flat.protocol.Consumer;

public class KafkaConsumer extends Consumer<byte[]> {

	private String zookeeperHost;
	private int zookeeperPort;
	private String topic;
	
	private int counter = 0;
	
	private de.fzi.cep.sepa.commons.messaging.kafka.KafkaConsumerGroup kafkaConsumerGroup;
	
	public KafkaConsumer(String zookeeperHost, int zookeeperPort, String topic, DatatypeDefinition dataType) {
		super(dataType);
		this.zookeeperHost = zookeeperHost;
		this.zookeeperPort = zookeeperPort;
		this.topic = topic;
	}
	
	@Override
	public void openConsumer() {
		System.out.println("Datatype is" +dataType.getClass().getCanonicalName());
		kafkaConsumerGroup = new KafkaConsumerGroup(zookeeperHost +":" +zookeeperPort, RandomStringUtils.randomAlphabetic(6),
				new String[] {topic}, this);
		kafkaConsumerGroup.run(1);
	}

	@Override
	public void closeConsumer() {
		kafkaConsumerGroup.shutdown();
	}

	@Override
	public void onEvent(byte[] payload) {
		notify(dataType.unmarshal(payload));	
		counter++;
		if (counter % 100000 == 0) System.out.println(counter + " Events sent.");
	}

}
