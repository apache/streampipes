
package de.fzi.cep.sepa.commons.messaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import kafka.producer.KeyedMessage;
import kafka.producer.Producer;
import kafka.producer.ProducerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.collection.JavaConversions;

public class ProaSenseInternalProducer {

    private static final Logger log = LoggerFactory.getLogger(ProaSenseInternalProducer.class);

    private final String brokerUrl;
    private final String producerTopic;
    
    private Producer<String, String> producer;

    public ProaSenseInternalProducer(String brokerUrl, String producerTopic) {
    	this.brokerUrl = brokerUrl;
    	this.producerTopic = producerTopic;
    	
        log.info("Initializing Kafka ...");

        producer = new Producer<>(createProducerConfig());

        log.info("Kafka initialized!");
    }

    public void send(List<String> strMsgs) {
        
            log.debug("Sending message: " + strMsgs);

        List<KeyedMessage<String, String>> msgs = new ArrayList<>(strMsgs.size());
        for (int i = 0; i < strMsgs.size(); i++) {
            String msgStr = strMsgs.get(i);
            msgs.add(new KeyedMessage<String, String>(producerTopic, brokerUrl, msgStr));
        }

        producer.send(JavaConversions.asScalaBuffer(msgs));
    }
    
    public void send(String msg) {
    	log.info("Sending event" +msg);
    	send(Arrays.asList(msg));
    }
    
    public void send(byte[] byteMsg)
    {
    	log.info("Sending Event");
    	List<byte[]> bytes = new ArrayList<>();
    	bytes.add(byteMsg);
    	
	     List<KeyedMessage<String, String>> msgs = new ArrayList<>(bytes.size());
	     for (int i = 0; i < bytes.size(); i++) {
	         byte[] msgStr = bytes.get(i);
	         
	         msgs.add(new KeyedMessage<String, String>(producerTopic, brokerUrl, new String(msgStr)));     
	     }
         producer.send(JavaConversions.asScalaBuffer(msgs));
    }

    public void shutdown() {
        if (producer != null)
            producer.close();
    }

    private ProducerConfig createProducerConfig() {
        Properties props = new Properties();
        props.put("metadata.broker.list", brokerUrl);
        props.put("serializer.class", "kafka.serializer.StringEncoder");
//        props.put("partitioner.class", "example.producer.SimplePartitioner");
        props.put("request.required.acks", "1");
        return new ProducerConfig(props);
    }
}

