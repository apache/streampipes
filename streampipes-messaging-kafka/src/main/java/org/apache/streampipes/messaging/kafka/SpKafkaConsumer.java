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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.messaging.EventConsumer;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.messaging.kafka.config.ConsumerConfigFactory;
import org.apache.streampipes.messaging.kafka.config.KafkaConfigAppender;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.WildcardTopicDefinition;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class SpKafkaConsumer implements EventConsumer, Runnable,
    Serializable {

  private String topic;
  private InternalEventProcessor<byte[]> eventProcessor;
  private final KafkaTransportProtocol protocol;
  private volatile boolean isRunning;

  private List<KafkaConfigAppender> appenders = new ArrayList<>();
  private KafkaConsumer<byte[], byte[]> consumer;

  private static final Logger LOG = LoggerFactory.getLogger(SpKafkaConsumer.class);

  public SpKafkaConsumer(KafkaTransportProtocol protocol) {
    this.protocol = protocol;
    this.topic = protocol.getTopicDefinition().getActualTopicName();
  }

  public SpKafkaConsumer(KafkaTransportProtocol protocol,
                         List<KafkaConfigAppender> appenders) {
    this(protocol);
    this.appenders = appenders;
  }

  @Override
  public void run() {
    Duration duration = Duration.of(100, ChronoUnit.MILLIS);
    while (isRunning) {
      ConsumerRecords<byte[], byte[]> records = consumer.poll(duration);
      records.forEach(record -> eventProcessor.onEvent(record.value()));
    }
    consumer.close();
  }

  @Override
  public void connect(InternalEventProcessor<byte[]> eventProcessor) throws SpRuntimeException {
    LOG.info("Kafka consumer: Connecting to {}", protocol.getTopicDefinition().getActualTopicName());
    var patternTopic = isPatternTopic();
    this.eventProcessor = eventProcessor;
    this.isRunning = true;
    Properties props = makeProperties(protocol, appenders);

    consumer = new KafkaConsumer<>(props);
    var latch = new CountDownLatch(1);
    if (!patternTopic) {
      consumer.subscribe(Collections.singletonList(topic), new RebalanceListener(latch));
    } else {
      topic = replaceWildcardWithPatternFormat(topic);
      consumer.subscribe(Pattern.compile(topic), new RebalanceListener(latch));
    }

    Thread thread = new Thread(this);
    thread.start();
    try {
      if (!latch.await(10, TimeUnit.SECONDS)) {
        throw new SpRuntimeException("Timeout while waiting for partition assignment");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new SpRuntimeException("Interrupted while waiting for partition assignment", e);
    }
  }

  @Override
  public void disconnect() throws SpRuntimeException {
    LOG.info("Kafka consumer: Disconnecting from {}", topic);
    this.isRunning = false;
  }

  @Override
  public boolean isConnected() {
    return isRunning;
  }

  private boolean isPatternTopic() {
    return this.protocol.getTopicDefinition() instanceof WildcardTopicDefinition;
  }

  private String replaceWildcardWithPatternFormat(String topic) {
    topic = topic.replaceAll("\\.", "\\\\.");
    return topic.replaceAll("\\*", ".*");
  }

  private Properties makeProperties(KafkaTransportProtocol protocol,
                                    List<KafkaConfigAppender> appenders) {
    return new ConsumerConfigFactory(protocol).buildProperties(appenders);
  }

  private class RebalanceListener implements ConsumerRebalanceListener {

    private final CountDownLatch latch;
    public RebalanceListener(CountDownLatch latch) {
      this.latch = latch;
    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> collection) {
      consumer.pause(collection);
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
      consumer.resume(partitions);
      latch.countDown();
    }
  }
}
