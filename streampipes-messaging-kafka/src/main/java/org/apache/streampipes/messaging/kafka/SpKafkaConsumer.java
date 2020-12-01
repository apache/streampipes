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

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.messaging.EventConsumer;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.messaging.kafka.config.ConsumerConfigFactory;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.grounding.WildcardTopicDefinition;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.regex.Pattern;

public class SpKafkaConsumer implements EventConsumer<KafkaTransportProtocol>, Runnable,
        Serializable {

  private String topic;
  private InternalEventProcessor<byte[]> eventProcessor;
  private KafkaTransportProtocol protocol;
  private String username;
  private String password;
  private volatile boolean isRunning;
  private Boolean patternTopic = false;

  private static final Logger LOG = LoggerFactory.getLogger(SpKafkaConsumer.class);

  public SpKafkaConsumer() {

  }

  public SpKafkaConsumer(KafkaTransportProtocol protocol, String topic, InternalEventProcessor<byte[]> eventProcessor) {
      this.protocol = protocol;
      this.username = null;
      this.password = null;
      this.topic = topic;
      this.eventProcessor = eventProcessor;
      this.isRunning = true;
  }


  public SpKafkaConsumer(KafkaTransportProtocol protocol, String topic, InternalEventProcessor<byte[]> eventProcessor,
                         String username, String password) {
    this.protocol = protocol;
    this.topic = topic;
    this.eventProcessor = eventProcessor;
    this.isRunning = true;
    this.username = username;
    this.password = password;
  }


  // TODO backwards compatibility, remove later
  public SpKafkaConsumer(String kafkaUrl, String topic, InternalEventProcessor<byte[]> callback) {
    KafkaTransportProtocol protocol = new KafkaTransportProtocol();
    protocol.setKafkaPort(Integer.parseInt(kafkaUrl.split(":")[1]));
    protocol.setBrokerHostname(kafkaUrl.split(":")[0]);
    protocol.setTopicDefinition(new SimpleTopicDefinition(topic));

    try {
      this.connect(protocol, callback);
    } catch (SpRuntimeException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    Properties props;
    if (username != null && password != null) {
      props = makePropertiesSaslPlain(protocol, username, password);
    }
    else {
      props = makeProperties(protocol);
    }
    KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(props);
    if (!patternTopic) {
      consumer.subscribe(Collections.singletonList(topic));
    } else {
      topic = replaceWildcardWithPatternFormat(topic);
      consumer.subscribe(Pattern.compile(topic), new ConsumerRebalanceListener() {
        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
          // TODO
        }

        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
          // TODO
        }
      });
    }
    while (isRunning) {
      ConsumerRecords<String, byte[]> records = consumer.poll(100);
      for (ConsumerRecord<String, byte[]> record : records) {
        eventProcessor.onEvent(record.value());
      }
    }
    LOG.info("Closing Kafka Consumer.");
    consumer.close();
  }

  private String replaceWildcardWithPatternFormat(String topic) {
    topic = topic.replaceAll("\\.", "\\\\.");
    return topic.replaceAll("\\*", ".*");
  }

  private Properties makeProperties(KafkaTransportProtocol protocol) {
    return new ConsumerConfigFactory(protocol).makeProperties();
  }

  private Properties makePropertiesSaslPlain(KafkaTransportProtocol protocol, String username, String password) {
    return new ConsumerConfigFactory(protocol).makePropertiesSaslPlain(username, password);
  }

  @Override
  public void connect(KafkaTransportProtocol protocol, InternalEventProcessor<byte[]>
          eventProcessor)
          throws SpRuntimeException {
    LOG.info("Kafka consumer: Connecting to " + protocol.getTopicDefinition().getActualTopicName());
    if (protocol.getTopicDefinition() instanceof WildcardTopicDefinition) {
      this.patternTopic = true;
    }
    this.eventProcessor = eventProcessor;
    this.protocol = protocol;
    this.topic = protocol.getTopicDefinition().getActualTopicName();
    this.isRunning = true;

    Thread thread = new Thread(this);
    thread.start();
  }

  @Override
  public void disconnect() throws SpRuntimeException {
    LOG.info("Kafka consumer: Disconnecting from " + topic);
    this.isRunning = false;

  }

  @Override
  public Boolean isConnected() {
    return isRunning;
  }
}
