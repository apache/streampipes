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
import de.fzi.cep.sepa.runtime.util.ThriftSerializer;
import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.storm.utils.StormUtils;
import de.fzi.cep.sepa.storm.utils.Utils;
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
    
    private Gson gson;
    private TSerializer serializer;
    private ActiveMQPublisher activeMqProducer;
    private ProaSenseInternalProducer kafkaProducer;
    private EventStream eventStream;
        
    public SinkSepaBolt(String id, EventStream eventStream) {
    	this.id = id;
		this.broker = eventStream.getEventGrounding().getTransportProtocol().toString();		
		this.topic = eventStream.getEventGrounding().getTransportProtocol().getTopicName();
		this.scheme = StormUtils.getScheme(eventStream);
		this.eventStream = eventStream;
		this.activeMqProducer = null;
		this.kafkaProducer = null;
	}
    

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {

    	if (eventStream.getEventGrounding().getTransportProtocol() instanceof KafkaTransportProtocol) {
    		this.kafkaProducer = new ProaSenseInternalProducer(broker, topic);
    	} else {
    		try {
				this.activeMqProducer = new ActiveMQPublisher(broker, topic);
			} catch (JMSException e) {
				e.printStackTrace();
			}
    	}

        this.gson = new Gson();
        this.serializer = new TSerializer(new TBinaryProtocol.Factory());

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
    }
    
    private void send(Map<String, Object> event) {
    	if (kafkaProducer != null) {
    		sendToKafka(event);
    	} else if (activeMqProducer != null) {
    		sendToJms(event);
    	}
    }


	private void sendToJms(Map<String, Object> event) {
		try {
			activeMqProducer.sendText(new String(toJsonOutputFormat(event)));
		} catch (JMSException e) {
			e.printStackTrace();
		};
	}

	private void sendToKafka(Map<String, Object> event)
	{
			kafkaProducer.send(toJsonOutputFormat(event));
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
		
		send(result);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
