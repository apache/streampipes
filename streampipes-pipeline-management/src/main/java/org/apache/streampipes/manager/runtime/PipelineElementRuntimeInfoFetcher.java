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

import com.google.inject.internal.cglib.core.$LocalVariablesSorter;
import org.apache.streampipes.messaging.kafka.SpKafkaConsumer;
import org.apache.streampipes.messaging.mqtt.MqttConsumer;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.messaging.jms.ActiveMQConsumer;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.TransportFormat;

import java.util.HashMap;
import java.util.Map;

public enum PipelineElementRuntimeInfoFetcher {
  INSTANCE;

  Logger logger = LoggerFactory.getLogger(PipelineElementRuntimeInfoFetcher.class);

  private int FETCH_INTERVAL_MS = 300;
  private Map<String, SpDataFormatConverter> converterMap;

  PipelineElementRuntimeInfoFetcher() {
    this.converterMap = new HashMap<>();
  }

  public String getCurrentData(SpDataStream spDataStream) throws SpRuntimeException {

    if (spDataStream.getEventGrounding().getTransportProtocol() instanceof KafkaTransportProtocol) {
      return getLatestEventFromKafka(spDataStream);
    }
    else if (spDataStream.getEventGrounding().getTransportProtocol() instanceof JmsTransportProtocol){
      return getLatestEventFromJms(spDataStream);
    } else {
      return getLatestEventFromMqtt(spDataStream);
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

  private String getLatestEventFromJms(SpDataStream spDataStream) throws SpRuntimeException {
    final String[] result = {null};
    final String topic = getOutputTopic(spDataStream);
    if (!converterMap.containsKey(topic)) {
      this.converterMap.put(topic,
              new SpDataFormatConverterGenerator(getTransportFormat(spDataStream)).makeConverter());
    }
    ActiveMQConsumer consumer = new ActiveMQConsumer();
    consumer.connect((JmsTransportProtocol) spDataStream.getEventGrounding().getTransportProtocol(), new InternalEventProcessor<byte[]>() {
      @Override
      public void onEvent(byte[] event) {
        try {
          result[0] = converterMap.get(topic).convert(event);
          consumer.disconnect();
        } catch (SpRuntimeException e) {
          e.printStackTrace();
        }
      }
    });

    while (result[0] == null) {
      try {
        Thread.sleep(FETCH_INTERVAL_MS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    return result[0];
  }

  private String getLatestEventFromMqtt(SpDataStream spDataStream) throws SpRuntimeException {
    final String[] result = {null};
    String mqttTopic = getOutputTopic(spDataStream);
    MqttTransportProtocol protocol = (MqttTransportProtocol) spDataStream.getEventGrounding().getTransportProtocol();

    if (!converterMap.containsKey(mqttTopic)){
      this.converterMap.put(mqttTopic, new SpDataFormatConverterGenerator(getTransportFormat(spDataStream)).makeConverter());
    }
    MqttConsumer mqttConsumer = new MqttConsumer();
    mqttConsumer.connect(protocol, new InternalEventProcessor<byte[]>() {
      @Override
      public void onEvent(byte[] event) {
        try {
          result[0] = converterMap.get(mqttTopic).convert(event);
          mqttConsumer.disconnect();
        } catch (SpRuntimeException e) {
          e.printStackTrace();
        }
      }
    });

    while (result[0] == null) {
      try {
        Thread.sleep(FETCH_INTERVAL_MS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    return result[0];
  }

  private String getLatestEventFromKafka(SpDataStream spDataStream) throws SpRuntimeException {
    final String[] result = {null};
    String kafkaTopic = getOutputTopic(spDataStream);
    KafkaTransportProtocol protocol = (KafkaTransportProtocol) spDataStream.getEventGrounding().getTransportProtocol();

    // Change kafka config when running in development mode
    if ("true".equals(System.getenv("SP_DEBUG"))) {
      protocol.setBrokerHostname("localhost");
      protocol.setKafkaPort(9094);
    }

    if (!converterMap.containsKey(kafkaTopic)) {
      this.converterMap.put(kafkaTopic,
              new SpDataFormatConverterGenerator(getTransportFormat(spDataStream)).makeConverter());
    }

    SpKafkaConsumer kafkaConsumer = new SpKafkaConsumer(protocol, kafkaTopic, new InternalEventProcessor<byte[]>() {
      @Override
      public void onEvent(byte[] event) {
        try {
          result[0] = converterMap.get(kafkaTopic).convert(event);
        } catch (SpRuntimeException e) {
          e.printStackTrace();
        }
      }
    });

    Thread t = new Thread(kafkaConsumer);
    t.start();

    long timeout = 0;
    while (result[0] == null && timeout < 6000) {
      try {
        Thread.sleep(FETCH_INTERVAL_MS);
        timeout = timeout + 300;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    kafkaConsumer.disconnect();

    return result[0];
  }

}
