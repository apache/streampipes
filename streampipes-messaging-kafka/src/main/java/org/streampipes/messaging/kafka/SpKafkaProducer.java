package org.streampipes.messaging.kafka;

import org.streampipes.messaging.EventProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.streampipes.model.impl.KafkaTransportProtocol;

import java.io.Serializable;
import java.util.Properties;

/**
 * Created by riemer on 01.10.2016.
 */
public class SpKafkaProducer implements EventProducer<KafkaTransportProtocol>, Serializable {

    private String brokerUrl;
    private String topic;
    private Producer<String, byte[]> producer;

    private Boolean connected;

    public void publish(byte[] message) {
        producer.send(new ProducerRecord<>(topic, message));
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

    @Override
    public void connect(KafkaTransportProtocol protocolSettings) {
        this.brokerUrl = protocolSettings.getBrokerHostname() +":" +protocolSettings.getKafkaPort();
        this.topic = protocolSettings.getTopicName();
        this.producer = new KafkaProducer<>(getProperties());
        this.connected = true;
    }

    @Override
    public void disconnect() {
        this.producer.close();
        this.connected = false;
    }

    @Override
    public Boolean isConnected() {
        return connected;
    }
}
