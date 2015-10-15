package de.fzi.cep.sepa.storm.topology;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.storm.guava.collect.Maps;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;
import de.fzi.cep.sepa.commons.messaging.activemq.ActiveMQPublisher;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.JmsTransportProtocol;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.storm.utils.StormUtils;
import de.fzi.cep.sepa.storm.utils.Utils;
import de.fzi.cep.sepa.util.ThriftSerializer;
import backtype.storm.spout.Scheme;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;


public class SinkSepaBolt<B extends BindingParameters> extends BaseRichBolt {

private static final long serialVersionUID = -3694170770048756860L;

	private String id;
	private String broker;
	private String topic;
	private Scheme scheme;

    private static Logger log = LoggerFactory.getLogger(SinkSepaBolt.class);
    
    private Map<String, KafkaProducer<String, byte[]>> kafkaProducers;
    private Map<String, ActiveMQPublisher> activeMqProducers;
    private Gson gson;
    private TSerializer serializer;
    private Map<String, B> boltSettings;
    private ProaSenseInternalProducer producer;
        
    public SinkSepaBolt(String id, EventStream eventStream) {
    	this.id = id;
		this.broker = Utils.getBroker(eventStream);		
		this.topic = Utils.getTopic(eventStream);
		this.scheme = StormUtils.getScheme(eventStream);
	}
    

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
    	boltSettings = new HashMap<>();

		producer = new ProaSenseInternalProducer(broker, topic);
        this.kafkaProducers = new HashMap<>();
        this.activeMqProducers = new HashMap<>();
        this.gson = new Gson();
        this.serializer = new TSerializer(new TBinaryProtocol.Factory());

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
    }


	private void sendToJms(Map<String, Object> event, B parameters,
			String configurationId) {
		try {
			activeMqProducers.get(configurationId).sendText(new String(toOutputFormat(event, parameters)));
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		};
	}

	private void sendToKafka(Map<String, Object> event)
	{
			producer.send(toJsonOutputFormat(event));
	}
	
	private byte[] toOutputFormat(Map<String, Object> event, B parameters) throws TException
	{
		if (StormUtils.isJson(parameters.getGraph().getOutputStream()))
			return toJsonOutputFormat(event);
		else 
			return toThriftOutputFormat(event);
	}
	
	
	private byte[] toThriftOutputFormat(Map<String, Object> event) throws TException {
		return serializer.serialize(new ThriftSerializer().toSimpleEvent(event));
	}

	private byte[] toJsonOutputFormat(Map<String, Object> event) {
		return gson.toJson(event).getBytes();
	}

	@Override
	public void execute(Tuple tuple) {
		Map<String, Object> result = new HashMap<>();

		List<String> fields = scheme.getOutputFields().toList();
		
		for (String field : fields) {
//			if (tuple.contains(field))
				result.put(field, tuple.getValueByField(field));
		}
		
		sendToKafka(result);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
