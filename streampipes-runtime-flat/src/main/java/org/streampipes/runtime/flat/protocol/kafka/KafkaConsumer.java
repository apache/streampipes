package org.streampipes.runtime.flat.protocol.kafka;

import org.streampipes.messaging.kafka.StreamPipesKafkaConsumer;
import org.streampipes.runtime.flat.datatype.DatatypeDefinition;
import org.streampipes.runtime.flat.protocol.Consumer;
import org.apache.commons.lang.RandomStringUtils;

import java.util.Properties;

public class KafkaConsumer extends Consumer<byte[]> {

    private String kafkaHost;
    private int kafkaPort;
    private String topic;

    private int counter = 0;

    private StreamPipesKafkaConsumer kafkaConsumerGroup;

    public KafkaConsumer(String kafkaHost, int kafkaPort, String topic, DatatypeDefinition dataType) {
        super(dataType);
        this.kafkaHost = kafkaHost;
        this.kafkaPort = kafkaPort;
        this.topic = topic;
    }

    @Override
    public void openConsumer() {

        try {
            kafkaConsumerGroup = new StreamPipesKafkaConsumer(kafkaHost + ":" + kafkaPort, topic, this);
            Thread thread = new Thread(kafkaConsumerGroup);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeConsumer() {
        kafkaConsumerGroup.close();
    }

    @Override
    public void onEvent(byte[] payload) {
        notify(dataType.unmarshal(payload));
        counter++;
        if (counter % 100000 == 0) System.out.println(counter + " Events sent.");
    }


    private Properties getConsumerProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaHost + ":" + kafkaPort);
        props.put("group.id", RandomStringUtils.randomAlphanumeric(6));
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        return props;
    }
}
