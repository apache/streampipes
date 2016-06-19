
package de.fzi.cep.sepa.commons.messaging;

import java.util.HashMap;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;


public class ProaSenseInternalProducer implements IMessagePublisher<byte[]> {

    private static final Logger log = LoggerFactory.getLogger(ProaSenseInternalProducer.class);

    private final String producerTopic;
    
    private static int i = 0;
    
    private KafkaProducer<String, byte[]> kafkaProducer;

    public ProaSenseInternalProducer(String brokerUrl, String producerTopic) {
    	this.producerTopic = producerTopic;
        HashMap<String, Object> kafkaConfig = new HashMap<>();
        kafkaConfig.put("bootstrap.servers", brokerUrl);
        kafkaConfig.put("batch.size", ClientConfiguration.INSTANCE.getKafkaBatchSize());
        kafkaConfig.put("linger.ms", ClientConfiguration.INSTANCE.getKafkaLingerMs());
        kafkaConfig.put("acks", ClientConfiguration.INSTANCE.getKafkaAcks());
        this.kafkaProducer = new KafkaProducer<>(kafkaConfig, new StringSerializer(), new ByteArraySerializer());

    }
  
    public void send(byte[] byteMsg)
    {	
    	try {
    	ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(producerTopic, byteMsg);
    	i++;
    	//if (i % 500 == 0) System.out.println(i +"events sent."); 
    	kafkaProducer.send(record);
    	
    	} catch(Exception e) { e.printStackTrace();}
    }

    public void shutdown() {
        if (kafkaProducer != null)
            kafkaProducer.close();
    }

	@Override
	public void publish(byte[] message) {
		send(message);
	}

}

