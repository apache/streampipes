package de.fzi.cep.sepa.storm.messaging;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.fzi.cep.sepa.commons.messaging.IMessageListener;


public class KafkaConsumerGroup {

	private final ConsumerConnector consumer;
	private final String[] topics;
	private ExecutorService executor;
	private IMessageListener listener;

	public KafkaConsumerGroup(String a_zookeeper, String a_groupId,
			String[] topics, IMessageListener listener) {
		consumer = kafka.consumer.Consumer
				.createJavaConsumerConnector(createConsumerConfig(a_zookeeper,
						a_groupId));
		this.topics = topics;
		this.listener = listener;
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
				executor.submit(new KafkaConsumer(stream, threadNumber, topic, listener));
				threadNumber++;
			}
		}
	}

	private static ConsumerConfig createConsumerConfig(String a_zookeeper,
			String a_groupId) {
		Properties props = new Properties();
		props.put("zookeeper.connect", a_zookeeper);
		props.put("group.id", a_groupId);
		props.put("zookeeper.session.timeout.ms", "10000");
		props.put("zk.sessiontimeout.ms", "10000");
		props.put("zookeeper.sync.time.ms", "10000");
		props.put("auto.commit.interval.ms", "1000");

		return new ConsumerConfig(props);
	}

}
