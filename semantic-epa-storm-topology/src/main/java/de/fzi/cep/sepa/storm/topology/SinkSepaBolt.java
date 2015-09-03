package de.fzi.cep.sepa.storm.topology;

import java.net.URI;
import java.util.HashMap;
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

import de.fzi.cep.sepa.commons.messaging.activemq.ActiveMQPublisher;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.JmsTransportProtocol;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.storm.controller.ConfigurationMessage;
import de.fzi.cep.sepa.storm.controller.Operation;
import de.fzi.cep.sepa.util.ThriftSerializer;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;


public class SinkSepaBolt<B extends BindingParameters> extends SepaBolt<B> {

private static final long serialVersionUID = -3694170770048756860L;
    
    private static Logger log = LoggerFactory.getLogger(SinkSepaBolt.class);
    
    private Map<String, KafkaProducer<String, byte[]>> kafkaProducers;
    private Map<String, ActiveMQPublisher> activeMqProducers;
    private Map<String, String> topics;
    private Gson gson;
    private TSerializer serializer;
        
    public SinkSepaBolt(String id) {
        super(id);
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        super.prepare(map, topologyContext, outputCollector);
        this.kafkaProducers = new HashMap<>();
        this.topics = new HashMap<>();
        this.activeMqProducers = new HashMap<>();
        this.gson = new Gson();
        this.serializer = new TSerializer(new TBinaryProtocol.Factory());

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        //outputFieldsDeclarer.declareStream("abc", new Fields("payload"));
    }

	@Override
	protected void performEventAction(Map<String, Object> event, B parameters, String configurationId) {
		event.remove("configurationId");
		if (kafkaProducers.containsKey(configurationId)) sendToKafka(event, parameters, configurationId);
		else if (activeMqProducers.containsKey(configurationId)) sendToJms(event, parameters, configurationId);
	}
	
	private void sendToJms(Map<String, Object> event, B parameters,
			String configurationId) {
		try {
			activeMqProducers.get(configurationId).sendText(new String(toOutputFormat(event, parameters)));
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	}

	private void sendToKafka(Map<String, Object> event, B parameters, String configurationId)
	{
		try {
			kafkaProducers.get(configurationId).send(new ProducerRecord<String, byte[]>(topics.get(configurationId), toOutputFormat(event, parameters)));
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private byte[] toOutputFormat(Map<String, Object> event, B parameters) throws TException
	{
		if (parameters.getGraph().getOutputStream().getEventGrounding().getTransportFormats().get(0).getRdfType().contains(URI.create(MessageFormat.Json)))
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
	protected void performConfigAction(ConfigurationMessage<B> params)
	{
		super.performConfigAction(params);
		if (params.getOperation() == Operation.BIND)
		{
			EventGrounding outputGrounding = params.getBindingParameters().getGraph().getOutputStream().getEventGrounding();
			if (outputGrounding.getTransportProtocol() instanceof KafkaTransportProtocol) addKafkaProducer(params, (KafkaTransportProtocol) outputGrounding.getTransportProtocol());
			else if (outputGrounding.getTransportProtocol() instanceof JmsTransportProtocol) addJmsProducer(params, (JmsTransportProtocol) outputGrounding.getTransportProtocol());
			String outputBrokerTopic = outputGrounding.getTransportProtocol().getTopicName();
	        topics.put(params.getConfigurationId(), outputBrokerTopic);
	        System.out.println("bind");
		}
		else if (params.getOperation() == Operation.DETACH)
		{
			System.out.println("detach");
			if (kafkaProducers.containsKey(params.getConfigurationId())) kafkaProducers.remove(params.getConfigurationId());
			if (activeMqProducers.containsKey(params.getConfigurationId())) activeMqProducers.remove(params.getConfigurationId());
			topics.remove(params.getConfigurationId());
		}
	}
	
	private void addJmsProducer(ConfigurationMessage<B> params,
			JmsTransportProtocol protocol) {
		try {
			ActiveMQPublisher publisher = new ActiveMQPublisher(protocol.getBrokerHostname() +":" +protocol.getPort(), protocol.getTopicName());
			activeMqProducers.put(params.getConfigurationId(), publisher);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addKafkaProducer(ConfigurationMessage<B> params, KafkaTransportProtocol protocol)
	{
		String outputBrokerUrl = protocol.getBrokerHostname();
		int outputBrokerPort = protocol.getKafkaPort();
        HashMap<String, Object> kafkaConfig = Maps.newHashMap();
        kafkaConfig.put("bootstrap.servers", outputBrokerUrl +":" +outputBrokerPort);
        KafkaProducer<String, byte[]> kafkaProducer = new KafkaProducer<>(kafkaConfig, new StringSerializer(), new ByteArraySerializer());
        kafkaProducers.put(params.getConfigurationId(), kafkaProducer);
	}
}
