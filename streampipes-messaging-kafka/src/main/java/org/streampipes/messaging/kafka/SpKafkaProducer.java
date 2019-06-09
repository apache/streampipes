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
import org.apache.kafka.common.config.TopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.kafka.config.ProducerConfigFactory;
import org.streampipes.model.grounding.KafkaTransportProtocol;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class SpKafkaProducer implements EventProducer<KafkaTransportProtocol>, Serializable {

  private static final String COLON = ":";

  private String brokerUrl;
  private String topic;
  private Producer<String, byte[]> producer;

  private Boolean connected;

  private static final Logger LOG = LoggerFactory.getLogger(SpKafkaProducer.class);

  public SpKafkaProducer() {

  }

  // TODO backwards compatibility, remove later
  public SpKafkaProducer(String url, String topic) {
    String[] urlParts = url.split(COLON);
    KafkaTransportProtocol protocol = new KafkaTransportProtocol(urlParts[0],
            Integer.parseInt(urlParts[1]), topic);
    this.brokerUrl = url;
    this.topic = topic;
    this.producer = new KafkaProducer<>(makeProperties(protocol));
  }

  public void publish(String message) {
    publish(message.getBytes());
  }

  public void publish(byte[] message) {
    producer.send(new ProducerRecord<>(topic, message));
  }

  private Properties makeProperties(KafkaTransportProtocol protocol) {
    return new ProducerConfigFactory(protocol).makeProperties();
  }

  @Override
  public void connect(KafkaTransportProtocol protocol) {
    LOG.info("Kafka producer: Connecting to " + protocol.getTopicDefinition().getActualTopicName());
    this.brokerUrl = protocol.getBrokerHostname() + ":" + protocol.getKafkaPort();
    this.topic = protocol.getTopicDefinition().getActualTopicName();

    createKafaTopic(protocol);

    this.producer = new KafkaProducer<>(makeProperties(protocol));
    this.connected = true;
  }

  /**
   * Create a new topic and define number partitions, replicas, and retention time
   *
   * @param settings
   */
  private void createKafaTopic(KafkaTransportProtocol settings) {
    String zookeeperHost = settings.getZookeeperHost() + ":" + settings.getZookeeperPort();

    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);

    Map<String, String> topicConfig = new HashMap<>();
    topicConfig.put(TopicConfig.RETENTION_MS_CONFIG, "600000");

    AdminClient adminClient = KafkaAdminClient.create(props);

    final NewTopic newTopic = new NewTopic(topic, 1, (short) 1);
    newTopic.configs(topicConfig);

    final CreateTopicsResult createTopicsResult = adminClient.createTopics(Collections.singleton(newTopic));

    try {
      createTopicsResult.values().get(topic).get();
    } catch (InterruptedException | ExecutionException e) {
      LOG.error("Could not create topic: " + topic + " on broker " + zookeeperHost);
    }
  }

  @Override
  public void disconnect() {
    LOG.info("Kafka producer: Disconnecting from " + topic);
    this.producer.close();
    this.connected = false;
  }

  @Override
  public Boolean isConnected() {
    return connected != null && connected;
  }
}
