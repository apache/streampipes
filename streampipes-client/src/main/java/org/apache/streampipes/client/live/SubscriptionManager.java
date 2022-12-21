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
package org.apache.streampipes.client.live;

import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.SpDataFormatDefinition;
import org.apache.streampipes.dataformat.SpDataFormatFactory;
import org.apache.streampipes.messaging.kafka.SpKafkaConsumer;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;

import java.util.Optional;

public class SubscriptionManager {

  private final EventGrounding grounding;
  private final EventProcessor callback;
  private final StreamPipesClientConfig clientConfig;

  private KafkaConfig kafkaConfig;
  private boolean overrideKafkaSettings = false;

  public SubscriptionManager(StreamPipesClientConfig clientConfig,
                             EventGrounding grounding,
                             EventProcessor callback) {
    this.grounding = grounding;
    this.callback = callback;
    this.clientConfig = clientConfig;
  }

  public SubscriptionManager(StreamPipesClientConfig clientConfig,
                             KafkaConfig kafkaConfig,
                             EventGrounding grounding,
                             EventProcessor callback) {
    this(clientConfig, grounding, callback);
    this.kafkaConfig = kafkaConfig;
    this.overrideKafkaSettings = true;
  }

  public SpKafkaConsumer subscribe() {
    Optional<SpDataFormatFactory> formatConverterOpt = this
        .clientConfig
        .getRegisteredDataFormats()
        .stream()
        .filter(format -> this.grounding
            .getTransportFormats()
            .get(0)
            .getRdfType()
            .stream()
            .anyMatch(tf -> tf.toString().equals(format.getTransportFormatRdfUri())))
        .findFirst();

    if (formatConverterOpt.isPresent()) {
      final SpDataFormatDefinition converter = formatConverterOpt.get().createInstance();

      KafkaTransportProtocol protocol =
          overrideKafkaSettings ? overrideHostname(getKafkaProtocol()) : getKafkaProtocol();
      SpKafkaConsumer kafkaConsumer = new SpKafkaConsumer(protocol, getOutputTopic(), event -> {
        try {
          Event spEvent = EventFactory.fromMap(converter.toMap(event));
          callback.onEvent(spEvent);
        } catch (SpRuntimeException e) {
          e.printStackTrace();
        }
      });
      Thread t = new Thread(kafkaConsumer);
      t.start();
      return kafkaConsumer;
    } else {
      throw new SpRuntimeException(
          "No converter found for data format - did you add a format factory (client.registerDataFormat)?");
    }
  }

  private KafkaTransportProtocol overrideHostname(KafkaTransportProtocol protocol) {
    protocol.setBrokerHostname(kafkaConfig.getKafkaHost());
    protocol.setKafkaPort(kafkaConfig.getKafkaPort());
    return protocol;
  }

  private KafkaTransportProtocol getKafkaProtocol() {
    return (KafkaTransportProtocol) this.grounding.getTransportProtocol();
  }

  private String getOutputTopic() {
    return this.grounding
        .getTransportProtocol()
        .getTopicDefinition()
        .getActualTopicName();
  }
}
