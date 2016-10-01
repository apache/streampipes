package de.fzi.cep.sepa.messaging.kafka;

import de.fzi.cep.sepa.messaging.EventProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.Serializable;
import java.util.Properties;

/**
 * Created by riemer on 01.10.2016.
 */
public class StreamPipesKafkaProducer implements EventProducer, Serializable {

    private String brokerUrl;
    private String topic;
    private Producer<String, byte[]> producer;

    public StreamPipesKafkaProducer(String brokerUrl, String topic) {
        this.brokerUrl = brokerUrl;
        this.topic = topic;
        openProducer();
    }

    @Override
    public void openProducer() {
        producer = new KafkaProducer<>(getProperties());
    }

    public void publish(byte[] message) {
        producer.send(new ProducerRecord<>(topic, message));
    }

    @Override
    public void publish(String event) {
        publish(event.getBytes());
    }

    @Override
    public void closeProducer() {
        producer.close();
    }


    private Properties getProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", brokerUrl);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        return props;
    }
}
