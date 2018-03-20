/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.messaging.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.messaging.EventProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.streampipes.model.grounding.KafkaTransportProtocol;

import java.io.Serializable;
import java.util.Properties;

public class SpKafkaProducer implements EventProducer<KafkaTransportProtocol>, Serializable {

    private String brokerUrl;
    private String topic;
    private Producer<String, byte[]> producer;

    private Boolean connected;

    private static final Logger LOG = LoggerFactory.getLogger(SpKafkaProducer.class);

    public SpKafkaProducer() {

    }

    // TODO backwards compatibility, remove later
    public SpKafkaProducer(String url, String topic) {
        this.brokerUrl = url;
        this.topic = topic;
        this.producer = new KafkaProducer<>(getProperties());
    }

    public void publish(String message) {
        publish(message.getBytes());
    }

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
        LOG.info("Kafka producer: Connecting to " +protocolSettings.getTopicDefinition().getActualTopicName());
        this.brokerUrl = protocolSettings.getBrokerHostname() +":" +protocolSettings.getKafkaPort();
        this.topic = protocolSettings.getTopicDefinition().getActualTopicName();
        this.producer = new KafkaProducer<>(getProperties());
        this.connected = true;
    }

    @Override
    public void disconnect() {
        LOG.info("Kafka producer: Disconnecting from " +topic);
        this.producer.close();
        this.connected = false;
    }

    @Override
    public Boolean isConnected() {
        return connected != null && connected;
    }
}
