package de.fzi.cep.sepa.storm.topology;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.consumer.Whitelist;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;


import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.storm.controller.ConfigurationMessage;
import de.fzi.cep.sepa.storm.utils.Constants;
import de.fzi.cep.sepa.storm.utils.Serializer;

public class SepaSpout extends BaseRichSpout {

	private static final long serialVersionUID = 1402529305108380459L;
	
	public static final String SPOUT_ID = "SepaSpout";
	
	public static final String SEPA_DATA_STREAM = "SEPA_DATA_STREAM";
    public static final String SEPA_CONFIG_STREAM = "SEPA_CONFIG_STREAM";
    
    
    private final String sepaStreamTopicPrefix = Constants.SEPA_PREFIX;
    
    private final String sepaDataStreamTopicSuffix = Constants.SEPA_DATA;
    private final String sepaConfigStreamTopicSuffix = Constants.SEPA_CONFIG;

	protected String id;
	protected String zookeeperUrl;
	protected String sepaDataTopic;
	protected String sepaConfigTopic;
	protected String sepaWhitelistTopic;
	
    private ConsumerConnector consumer;
    long mesageCounter = 0;

    public SepaSpout(String id, String zookeeperUrl) {
        this.id = id;
        this.zookeeperUrl = "ipe-koi04.fzi.de:2181";
        this.sepaConfigTopic = sepaStreamTopicPrefix + sepaConfigStreamTopicSuffix;
        this.sepaDataTopic = sepaStreamTopicPrefix + sepaDataStreamTopicSuffix;    
        //this.sepaWhitelistTopic = sepaStreamTopicPrefix +"*.*";
        this.sepaWhitelistTopic = "de.fzi.cep.sepa.storm.*";
    }

    public String getId() {
        return id;
    }

    private ConsumerIterator<byte[], byte[]> consumerIterator;

    protected SpoutOutputCollector collector;

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream(SEPA_DATA_STREAM, new Fields("payload"));
        outputFieldsDeclarer.declareStream(SEPA_CONFIG_STREAM, new Fields("config"));
    }

    @SuppressWarnings("rawtypes")
	@Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {

        this.collector = spoutOutputCollector;
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(
                createConsumerConfig());
        List<KafkaStream<byte[], byte[]>> streams = consumer.createMessageStreamsByFilter(new Whitelist(sepaWhitelistTopic));
        
        KafkaStream<byte[], byte[]> messageAndMetadatas = streams.get(0);
        this.consumerIterator = messageAndMetadatas.iterator();

    }

    private ConsumerConfig createConsumerConfig() {
        Properties props = new Properties();
        props.put("zookeeper.connect", "ipe-koi04.fzi.de:2181");
        props.put("group.id", id);
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
            if (isDataTopic(message.topic()))
            {
            	Map<String, Object> result;
				try {
					result = (Map<String, Object>) Serializer.deserialize(messagePayload);
	            	collector.emit(SEPA_DATA_STREAM, new Values(result));
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            	
            else if (isConfigTopic(message.topic()))
            {
            	ConfigurationMessage<? extends BindingParameters> msg;
				try {
					msg = (ConfigurationMessage<? extends BindingParameters>) Serializer.deserialize(messagePayload);
					collector.emit(SEPA_CONFIG_STREAM, new Values(msg));
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            }
        }
    }
    
    private boolean isConfigTopic(String topic)
    {
    	return topic.endsWith(".config");
    }
    
    private boolean isDataTopic(String topic)
    {
    	return topic.endsWith(".data");
    }

    @Override
    public void close() {
        this.consumer.shutdown();
    }
}
