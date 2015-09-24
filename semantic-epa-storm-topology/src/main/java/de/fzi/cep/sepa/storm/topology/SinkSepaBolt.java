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

import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;
import de.fzi.cep.sepa.commons.messaging.activemq.ActiveMQPublisher;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.JmsTransportProtocol;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.util.ThriftSerializer;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;


public class SinkSepaBolt<B extends BindingParameters> extends BaseRichBolt {

private static final long serialVersionUID = -3694170770048756860L;

	private String id;
    
    private static Logger log = LoggerFactory.getLogger(SinkSepaBolt.class);
    
    private Map<String, KafkaProducer<String, byte[]>> kafkaProducers;
    private Map<String, ActiveMQPublisher> activeMqProducers;
    private String topic;
    private Gson gson;
    private TSerializer serializer;
    private Map<String, B> boltSettings;
    private ProaSenseInternalProducer producer;
        
    public SinkSepaBolt(String id) {
        this.id = id;
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
    	// TODO fix the prepare methode
//        super.prepare(map, topologyContext, outputCollector);
    	boltSettings = new HashMap<>();

        this.topic = "new.data.storm.yeah.stream";

		producer = new ProaSenseInternalProducer("ipe-koi04.fzi.de:9092", topic);
        this.kafkaProducers = new HashMap<>();
        // TODO set topic
        this.activeMqProducers = new HashMap<>();
        this.gson = new Gson();
        this.serializer = new TSerializer(new TBinaryProtocol.Factory());

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        //outputFieldsDeclarer.declareStream("abc", new Fields("payload"));
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
//			kafkaProducers.get(configurationId).send(new ProducerRecord<String, byte[]>(topic, toOutputFormat(event, parameters)));
			producer.send(toJsonOutputFormat(event));
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
	public void execute(Tuple tuple) {
		Map<String, Object> payload = (Map<String, Object>) tuple.getValueByField("payload");
		
		sendToKafka(payload, boltSettings.get(payload.get("configurationId")), (String) payload.get("configurationId"));
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
