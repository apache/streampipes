package de.fzi.cep.sepa.sources.samples.util;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.fzi.cep.sepa.sources.samples.config.AkerVariables;

public class KafkaConsumerGroup {

	private final ConsumerConnector consumer;
	private final String[] topics;
	private ExecutorService executor;

	public KafkaConsumerGroup(String a_zookeeper, String a_groupId,
			String[] topics) {
		consumer = kafka.consumer.Consumer
				.createJavaConsumerConnector(createConsumerConfig(a_zookeeper,
						a_groupId));
		this.topics = topics;
	}

	public void shutdown() {
		if (consumer != null)
			consumer.shutdown();
		if (executor != null)
			executor.shutdown();
	}

	public void run(int a_numThreads) {
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		for(String topic : topics)
		{
			topicCountMap.put(topic, new Integer(a_numThreads));
		}
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer
				.createMessageStreams(topicCountMap);
		
		for(String topic : topics)
		{
			List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
	
			// now launch all the threads
			//
			executor = Executors.newFixedThreadPool(a_numThreads);
	
			// now create an object to consume the messages
			//
			int threadNumber = 0;
			for (final KafkaStream stream : streams) {
				executor.submit(new KafkaConsumer(stream, threadNumber, topic));
				threadNumber++;
			}
		}
	}

	private static ConsumerConfig createConsumerConfig(String a_zookeeper,
			String a_groupId) {
		Properties props = new Properties();
		props.put("zookeeper.connect", a_zookeeper);
		props.put("group.id", a_groupId);
		props.put("zookeeper.session.timeout.ms", "400");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");

		return new ConsumerConfig(props);
	}

	public static void main(String[] args) {
		String zooKeeper = "nissatech.no-ip.org:2181";
		String groupId = "groupId";
		String[] topic = {AkerVariables.DrillingRPM.topic(), AkerVariables.DrillingTorque.topic(), AkerVariables.GearLubeOilTemperature.topic(), AkerVariables.HookLoad.topic(), AkerVariables.SwivelOilTemperature.topic()};
		int threads = 1;

		KafkaConsumerGroup example = new KafkaConsumerGroup(zooKeeper, groupId,
				topic);
		example.run(threads);

		try {
			Thread.sleep(100000);
		} catch (InterruptedException ie) {

		}
		example.shutdown();
	}
}
