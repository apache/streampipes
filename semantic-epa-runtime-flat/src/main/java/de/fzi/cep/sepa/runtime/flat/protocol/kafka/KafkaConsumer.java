package de.fzi.cep.sepa.runtime.flat.protocol.kafka;

import org.apache.commons.lang.RandomStringUtils;

import de.fzi.cep.sepa.commons.messaging.kafka.KafkaConsumerGroup;
import de.fzi.cep.sepa.runtime.flat.datatype.DatatypeDefinition;
import de.fzi.cep.sepa.runtime.flat.protocol.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.Arrays;
import java.util.Properties;

public class KafkaConsumer extends Consumer<byte[]> {

    private String zookeeperHost;
    private int zookeeperPort;
    private String topic;

    private int counter = 0;

    private de.fzi.cep.sepa.commons.messaging.kafka.KafkaConsumerGroup kafkaConsumerGroup;

    public KafkaConsumer(String zookeeperHost, int zookeeperPort, String topic, DatatypeDefinition dataType) {
        super(dataType);
        this.zookeeperHost = zookeeperHost;
        this.zookeeperPort = zookeeperPort;
        this.topic = topic;
    }

    @Override
    public void openConsumer() {

        try {
            kafkaConsumerGroup = new KafkaConsumerGroup(zookeeperHost + ":" + zookeeperPort, RandomStringUtils.randomAlphabetic(6),
                    new String[]{topic}, this);
            kafkaConsumerGroup.run(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeConsumer() {
        kafkaConsumerGroup.shutdown();
    }

    @Override
    public void onEvent(byte[] payload) {
        notify(dataType.unmarshal(payload));
        counter++;
        if (counter % 100000 == 0) System.out.println(counter + " Events sent.");
    }


    private Properties getConsumerProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", zookeeperHost + ":" + zookeeperPort);
        props.put("group.id", RandomStringUtils.randomAlphanumeric(6));
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        return props;
    }
}
