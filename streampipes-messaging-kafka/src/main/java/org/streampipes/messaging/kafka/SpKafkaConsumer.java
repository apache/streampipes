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

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.messaging.EventConsumer;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.messaging.kafka.config.ConsumerConfigFactory;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.grounding.SimpleTopicDefinition;
import org.streampipes.model.grounding.WildcardTopicDefinition;

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
  private volatile boolean isRunning;
  private Boolean patternTopic = false;

  private static final Logger LOG = LoggerFactory.getLogger(SpKafkaConsumer.class);

  public SpKafkaConsumer() {

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
    KafkaConsumer<String, byte[]> kafkaConsumer = new KafkaConsumer<>(getProperties());
    if (!patternTopic) {
      kafkaConsumer.subscribe(Collections.singletonList(topic));
    } else {
      topic = replaceWildcardWithPatternFormat(topic);
      kafkaConsumer.subscribe(Pattern.compile(topic), new ConsumerRebalanceListener() {
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
      ConsumerRecords<String, byte[]> records = kafkaConsumer.poll(100);
      for (ConsumerRecord<String, byte[]> record : records) {
        eventProcessor.onEvent(record.value());
      }
    }
    LOG.info("Closing Kafka Consumer.");
    kafkaConsumer.close();
  }

  private String replaceWildcardWithPatternFormat(String topic) {
    topic = topic.replaceAll("\\.", "\\\\.");
    return topic.replaceAll("\\*", ".*");
  }

  private Properties getProperties() {
    return new ConsumerConfigFactory(protocol).makeProperties();
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
