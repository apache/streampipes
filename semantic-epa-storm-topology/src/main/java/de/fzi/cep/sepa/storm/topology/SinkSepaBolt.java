package de.fzi.cep.sepa.storm.topology;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.storm.guava.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.storm.controller.ConfigurationMessage;
import de.fzi.cep.sepa.storm.controller.Operation;
import de.fzi.cep.sepa.storm.utils.Serializer;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;


public class SinkSepaBolt<B extends BindingParameters> extends SepaBolt<B> {

private static final long serialVersionUID = -3694170770048756860L;
    
    private static Logger log = LoggerFactory.getLogger(FunctionalSepaBolt.class);
    
    private Map<String, KafkaProducer<String, byte[]>> kafkaProducers;
    private Map<String, String> kafkaTopics;
        
    public SinkSepaBolt(String id) {
        super(id);
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        super.prepare(map, topologyContext, outputCollector);
        this.kafkaProducers = new HashMap<>();
        this.kafkaTopics = new HashMap<>();

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream("abc", new Fields("payload"));
    }

	@Override
	protected void performEventAction(Map<String, Object> event, B parameters, String configurationId) {
		try {
			event.remove("configurationId");
			kafkaProducers.get(configurationId).send(new ProducerRecord<String, byte[]>(kafkaTopics.get(configurationId), Serializer.serialize(event)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void performConfigAction(ConfigurationMessage<B> params)
	{
		if (params.getOperation() == Operation.BIND)
		{
			EventGrounding outputGrounding = params.getBindingParameters().getGraph().getOutputStream().getEventGrounding();
			String outputBrokerUrl = ((KafkaTransportProtocol) outputGrounding.getTransportProtocol()).getBrokerHostname();
			int outputBrokerPort = ((KafkaTransportProtocol) outputGrounding.getTransportProtocol()).getKafkaPort();
			String outputBrokerTopic = outputGrounding.getTransportProtocol().getTopicName();
	        HashMap<String, Object> kafkaConfig = Maps.newHashMap();
	        kafkaConfig.put("bootstrap.servers", outputBrokerUrl +":" +outputBrokerPort);
	        KafkaProducer<String, byte[]> kafkaProducer = new KafkaProducer<>(kafkaConfig, new StringSerializer(), new ByteArraySerializer());
	        kafkaProducers.put(params.getConfigurationId(), kafkaProducer);
	        kafkaTopics.put(params.getConfigurationId(), outputBrokerTopic);
		}
		else if (params.getOperation() == Operation.DETACH)
		{
			kafkaProducers.remove(params.getConfigurationId());
			kafkaTopics.remove(params.getConfigurationId());
		}
	}
}
