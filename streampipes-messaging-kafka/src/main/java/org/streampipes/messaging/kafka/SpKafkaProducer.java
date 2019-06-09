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

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.messaging.EventProducer;
import org.streampipes.model.grounding.KafkaTransportProtocol;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

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
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 1638400);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 20);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
        return props;
    }

    @Override
    public void connect(KafkaTransportProtocol protocolSettings) {
        LOG.info("Kafka producer: Connecting to " +protocolSettings.getTopicDefinition().getActualTopicName());
        this.brokerUrl = protocolSettings.getBrokerHostname() +":" + protocolSettings.getKafkaPort();
        this.topic = protocolSettings.getTopicDefinition().getActualTopicName();

        createKafaTopic(protocolSettings);

        this.producer = new KafkaProducer<>(getProperties());
        this.connected = true;
    }

    /**
     * Create a new topic and define number partitions, replicas, and retention time
     * @param settings
     */
    private void createKafaTopic(KafkaTransportProtocol settings) {
        String zookeeperHost = settings.getZookeeperHost() + ":" + settings.getZookeeperPort();

        Properties props = new Properties();
        props.put("bootstrap.servers", brokerUrl);

        Map<String, String> topicConfig = new HashMap<>();
        topicConfig.put("retention.ms", "600000");

        AdminClient adminClient = KafkaAdminClient.create(props);

        final NewTopic newTopic = new NewTopic(topic, 1, (short) 1);
        newTopic.configs(topicConfig);

        final CreateTopicsResult createTopicsResult = adminClient.createTopics(Collections.singleton(newTopic));

        try {
            createTopicsResult.values().get(topic).get();
        } catch (InterruptedException e) {
            LOG.error("Could not create topic: " + topic + " on broker " + zookeeperHost);
        } catch (ExecutionException e) {
            LOG.error("Could not create topic: " + topic + " on broker " + zookeeperHost);
        }
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
