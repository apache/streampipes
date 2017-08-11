package org.streampipes.messaging.kafka;

import org.streampipes.messaging.Consumer;
import org.streampipes.messaging.EventConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by riemer on 01.10.2016.
 */
public class StreamPipesKafkaConsumer implements Consumer, Runnable, Serializable {

    private String kafkaUrl;
    private String topic;
    private String groupId;
    private EventConsumer<byte[]> eventListener;
    private KafkaConsumer<String, byte[]> kafkaConsumer;
    private volatile boolean isRunning = true;

    public StreamPipesKafkaConsumer(String kafkaUrl, String topic, EventConsumer<byte[]> eventListener) {
        this.kafkaUrl = kafkaUrl;
        this.topic = topic;
        this.eventListener = eventListener;
        this.groupId = UUID.randomUUID().toString();
    }

    @Override
    public void run() {
        kafkaConsumer = new KafkaConsumer<>(getProperties());
        kafkaConsumer.subscribe(Arrays.asList(topic));
        while (isRunning) {
            ConsumerRecords<String, byte[]> records = kafkaConsumer.poll(100);
            for (ConsumerRecord<String, byte[]> record : records)
                eventListener.onEvent(record.value());
        }
        this.kafkaConsumer.close();
    }

    public void close() {
        this.isRunning = false;
    }

    private Properties getProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaUrl);
        props.put("group.id", groupId);
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "10000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        props.put("zookeeper.session.timeout.ms", "60000");
        props.put("zookeeper.sync.time.ms", "20000");
        return props;
    }
}
