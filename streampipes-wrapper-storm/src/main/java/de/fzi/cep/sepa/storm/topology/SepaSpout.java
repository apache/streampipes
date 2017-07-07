package de.fzi.cep.sepa.storm.topology;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import backtype.storm.spout.Scheme;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.storm.utils.StormUtils;
import de.fzi.cep.sepa.storm.utils.Utils;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

public class SepaSpout extends BaseRichSpout {

	private static final long serialVersionUID = 1402529305108380459L;




	protected String id;
	protected Scheme scheme;
	protected String zookeeper;
	protected String topic;

	private ConsumerConnector consumer;

	public SepaSpout(String id, EventStream eventStream) {
		this.id = id;
		this.scheme = StormUtils.getScheme(eventStream);
		this.zookeeper = Utils.getZookeeperUrl(eventStream);		
		this.topic = eventStream.getEventGrounding().getTransportProtocol().getTopicName();
	}


	private ConsumerIterator<byte[], byte[]> consumerIterator;

	protected SpoutOutputCollector collector;

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declareStream(Utils.SEPA_DATA_STREAM, scheme.getOutputFields());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
		this.collector = spoutOutputCollector;
		int numThreads = 1;
		
		consumer = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig(zookeeper, id));
		

		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(numThreads));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
		KafkaStream<byte[], byte[]> messageAndMetadatas = streams.get(0);
		this.consumerIterator = messageAndMetadatas.iterator();

	}

	private ConsumerConfig createConsumerConfig(String zookeeperUrl, String groupId) {
		Properties props = new Properties();
		props.put("zookeeper.connect", zookeeperUrl);
		props.put("group.id", groupId);
		props.put("zookeeper.session.timeout.ms", "60000");
		props.put("zookeeper.sync.time.ms", "20000");
		props.put("auto.commit.interval.ms", "10000");

		return new ConsumerConfig(props);
	}

	@Override
	public void nextTuple() {
		if (consumerIterator.hasNext()) {
			MessageAndMetadata<byte[], byte[]> message = consumerIterator.next();
			byte[] messagePayload = message.message();
			
			collector.emit(Utils.SEPA_DATA_STREAM, scheme.deserialize(messagePayload));

		}
	}

	@Override
	public void close() {
		this.consumer.shutdown();
	}

	public String getId() {
		return id;
	}

}
