
package de.fzi.cep.sepa.commons.messaging;

import java.util.Arrays;
import java.util.HashMap;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProaSenseInternalProducer {

    private static final Logger log = LoggerFactory.getLogger(ProaSenseInternalProducer.class);

    private final String producerTopic;
    
    private KafkaProducer<String, byte[]> kafkaProducer;

    public ProaSenseInternalProducer(String brokerUrl, String producerTopic) {
    	this.producerTopic = producerTopic;
    	
        HashMap<String, Object> kafkaConfig = new HashMap<>();
        kafkaConfig.put("bootstrap.servers", brokerUrl);
        this.kafkaProducer = new KafkaProducer<>(kafkaConfig, new StringSerializer(), new ByteArraySerializer());

    }
  
    public void send(byte[] byteMsg)
    {	
    	try {
    	System.out.println("send");
    	ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(producerTopic, byteMsg);
    	Arrays.toString(record.value());
    	
    	kafkaProducer.send(record);
    	} catch(Exception e) { e.printStackTrace();}
    }

    public void shutdown() {
        if (kafkaProducer != null)
            kafkaProducer.close();
    }

}

