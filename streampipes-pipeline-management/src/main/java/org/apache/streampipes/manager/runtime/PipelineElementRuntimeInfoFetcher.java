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
package org.apache.streampipes.manager.runtime;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.messaging.jms.ActiveMQConsumer;
import org.apache.streampipes.messaging.kafka.SpKafkaConsumer;
import org.apache.streampipes.messaging.mqtt.MqttConsumer;
import org.apache.streampipes.messaging.nats.NatsConsumer;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.NatsTransportProtocol;
import org.apache.streampipes.model.grounding.TransportFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public enum PipelineElementRuntimeInfoFetcher {
  INSTANCE;

  Logger logger = LoggerFactory.getLogger(PipelineElementRuntimeInfoFetcher.class);

  private static final int FETCH_INTERVAL_MS = 300;
  private final Map<String, SpDataFormatConverter> converterMap;
  private Environment env;

  PipelineElementRuntimeInfoFetcher() {
    this.converterMap = new HashMap<>();
    this.env = Environments.getEnvironment();
  }

  public String getCurrentData(SpDataStream spDataStream) throws SpRuntimeException {
    var topic = getOutputTopic(spDataStream);
    var protocol = spDataStream.getEventGrounding().getTransportProtocol();
    if (env.getSpDebug().getValueOrDefault()) {
      protocol.setBrokerHostname("localhost");
    }
    if (!converterMap.containsKey(topic)) {
      this.converterMap.put(topic,
          new SpDataFormatConverterGenerator(getTransportFormat(spDataStream)).makeConverter());
    }

    var converter = converterMap.get(topic);

    if (spDataStream.getEventGrounding().getTransportProtocol() instanceof KafkaTransportProtocol) {
      return getLatestEventFromKafka((KafkaTransportProtocol) protocol, converter, topic);
    } else if (spDataStream.getEventGrounding().getTransportProtocol() instanceof JmsTransportProtocol) {
      return getLatestEventFromJms((JmsTransportProtocol) protocol, converter);
    } else if (spDataStream.getEventGrounding().getTransportProtocol() instanceof MqttTransportProtocol) {
      return getLatestEventFromMqtt((MqttTransportProtocol) protocol, converter);
    } else {
      return getLatestEventFromNats((NatsTransportProtocol) protocol, converter);
    }
  }

  private TransportFormat getTransportFormat(SpDataStream spDataStream) {
    return spDataStream.getEventGrounding().getTransportFormats().get(0);
  }

  private String getOutputTopic(SpDataStream spDataStream) {
    return spDataStream
        .getEventGrounding()
        .getTransportProtocol()
        .getTopicDefinition()
        .getActualTopicName();
  }

  private void waitForEvent(String[] result) {
    long timeout = 0;
    while (result[0] == null && timeout < 6000) {
      try {
        Thread.sleep(FETCH_INTERVAL_MS);
        timeout = timeout + 300;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private String getLatestEventFromJms(JmsTransportProtocol protocol,
                                       SpDataFormatConverter converter) throws SpRuntimeException {
    final String[] result = {null};
    ActiveMQConsumer consumer = new ActiveMQConsumer();
    consumer.connect(protocol, event -> {
      result[0] = converter.convert(event);
      consumer.disconnect();
    });

    waitForEvent(result);

    return result[0];
  }

  private String getLatestEventFromMqtt(MqttTransportProtocol protocol,
                                        SpDataFormatConverter converter) throws SpRuntimeException {
    final String[] result = {null};
    MqttConsumer mqttConsumer = new MqttConsumer();
    mqttConsumer.connect(protocol, event -> {
      result[0] = converter.convert(event);
      mqttConsumer.disconnect();
    });

    waitForEvent(result);

    return result[0];
  }

  private String getLatestEventFromNats(NatsTransportProtocol protocol,
                                        SpDataFormatConverter converter) throws SpRuntimeException {
    final String[] result = {null};
    NatsConsumer natsConsumer = new NatsConsumer();
    natsConsumer.connect(protocol, event -> {
      result[0] = converter.convert(event);
      natsConsumer.disconnect();
    });

    waitForEvent(result);

    return result[0];
  }

  private String getLatestEventFromKafka(KafkaTransportProtocol protocol,
                                         SpDataFormatConverter converter,
                                         String topic) throws SpRuntimeException {
    final String[] result = {null};
    // Change kafka config when running in development mode
    if (getEnvironment().getSpDebug().getValueOrDefault()) {
      protocol.setKafkaPort(9094);
    }

    SpKafkaConsumer kafkaConsumer = new SpKafkaConsumer(protocol, topic, event -> {
      result[0] = converter.convert(event);
    });

    Thread t = new Thread(kafkaConsumer);
    t.start();

    waitForEvent(result);

    kafkaConsumer.disconnect();

    return result[0];
  }

  private Environment getEnvironment() {
    return Environments.getEnvironment();
  }

}
