/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.messaging.kafka;

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.messaging.EventProducer;
import org.apache.streampipes.messaging.kafka.config.KafkaConfigAppender;
import org.apache.streampipes.messaging.kafka.config.ProducerConfigFactory;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.TopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class SpKafkaProducer implements EventProducer, Serializable {


  private static final String COLON = ":";

  private String brokerUrl;
  private String topic;
  private Producer<String, byte[]> producer;
  private KafkaTransportProtocol protocol;

  private boolean connected = false;

  private static final Logger LOG = LoggerFactory.getLogger(SpKafkaProducer.class);

  public SpKafkaProducer(KafkaTransportProtocol protocol) {
    this.protocol = protocol;
  }

  // TODO backwards compatibility, remove later
  public SpKafkaProducer(String url,
                         String topic,
                         List<KafkaConfigAppender> appenders) {
    String[] urlParts = url.split(COLON);
    KafkaTransportProtocol protocol = new KafkaTransportProtocol(urlParts[0],
        Integer.parseInt(urlParts[1]), topic);
    this.brokerUrl = url;
    this.topic = topic;
    this.producer = new KafkaProducer<>(makeProperties(protocol, appenders));
    this.connected = true;
  }

  public void publish(String message) {
    publish(message.getBytes());
  }

  public void publish(byte[] message) {
    if (connected) {
      producer.send(new ProducerRecord<>(topic, message));
    }
  }

  private Properties makeProperties(KafkaTransportProtocol protocol,
                                    List<KafkaConfigAppender> appenders) {
    return new ProducerConfigFactory(protocol).buildProperties(appenders);
  }

  @Override
  public void connect() {
    LOG.info("Kafka producer: Connecting to " + protocol.getTopicDefinition().getActualTopicName());
    this.brokerUrl = protocol.getBrokerHostname() + ":" + protocol.getKafkaPort();
    this.topic = protocol.getTopicDefinition().getActualTopicName();
    String zookeeperHost = protocol.getZookeeperHost() + ":" + protocol.getZookeeperPort();

    try {
      createKafkaTopic(protocol);
    } catch (ExecutionException | InterruptedException e) {
      LOG.error("Could not create topic: " + topic + " on broker " + zookeeperHost);
    }

    this.producer = new KafkaProducer<>(makeProperties(protocol, Collections.emptyList()));
    this.connected = true;

    LOG.info("Successfully created Kafka producer for topic " + this.topic);
  }

  /**
   * Create a new topic and define number partitions, replicas, and retention time
   *
   * @param settings The settings to connect to a Kafka broker
   */
  private void createKafkaTopic(KafkaTransportProtocol settings) throws ExecutionException, InterruptedException {

    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);

    AdminClient adminClient = KafkaAdminClient.create(props);

    ListTopicsResult topics = adminClient.listTopics();

    if (!topicExists(topics)) {
      Map<String, String> topicConfig = new HashMap<>();
      String retentionTime = Environments.getEnvironment().getKafkaRetentionTimeMs().getValueOrDefault();
      topicConfig.put(TopicConfig.RETENTION_MS_CONFIG, retentionTime);

      final NewTopic newTopic = new NewTopic(topic, 1, (short) 1);
      newTopic.configs(topicConfig);

      final CreateTopicsResult createTopicsResult = adminClient.createTopics(Collections.singleton(newTopic));
      createTopicsResult.values().get(topic).get();
      LOG.info("Successfully created Kafka topic " + topic);

    } else {
      LOG.info("Topic " + topic + "already exists in the broker, skipping topic creation");
    }
  }

  @Override
  public void disconnect() {
    LOG.info("Kafka producer: Disconnecting from " + topic);
    this.connected = false;
    this.producer.close();
  }

  @Override
  public boolean isConnected() {
    return connected;
  }

  private boolean topicExists(ListTopicsResult topicsInKafka) {
    try {
      return topicsInKafka.names().get().stream().anyMatch(t -> t.equals(topic));
    } catch (InterruptedException | ExecutionException e) {
      LOG.error("Could not fetch existing topics", e);
      return false;
    }
  }
}
