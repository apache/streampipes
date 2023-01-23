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
package org.apache.streampipes.manager.matching;

import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.manager.util.TopicGenerator;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.config.SpProtocol;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.NatsTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.grounding.TopicDefinition;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.message.DataSetModificationMessage;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Collections;

public class DataSetGroundingSelector {

  private SpDataSet spDataSet;

  public DataSetGroundingSelector(SpDataSet spDataSet) {
    this.spDataSet = spDataSet;
  }

  public DataSetModificationMessage selectGrounding() {
    EventGrounding outputGrounding = new EventGrounding();

    String topic = TopicGenerator.generateRandomTopic();
    TopicDefinition topicDefinition = new SimpleTopicDefinition(topic);

    SpProtocol prioritizedProtocol =
        BackendConfig.INSTANCE.getMessagingSettings().getPrioritizedProtocols().get(0);

    if (isPrioritized(prioritizedProtocol, JmsTransportProtocol.class)) {
      outputGrounding.setTransportProtocol(makeTransportProtocol(
          BackendConfig.INSTANCE.getJmsHost(),
          BackendConfig.INSTANCE.getJmsPort(),
          topicDefinition,
          JmsTransportProtocol.class
      ));
    } else if (isPrioritized(prioritizedProtocol, KafkaTransportProtocol.class)) {
      outputGrounding.setTransportProtocol(makeTransportProtocol(
          BackendConfig.INSTANCE.getKafkaHost(),
          BackendConfig.INSTANCE.getKafkaPort(),
          topicDefinition,
          KafkaTransportProtocol.class
      ));
    } else if (isPrioritized(prioritizedProtocol, MqttTransportProtocol.class)) {
      outputGrounding.setTransportProtocol(makeTransportProtocol(
          BackendConfig.INSTANCE.getMqttHost(),
          BackendConfig.INSTANCE.getMqttPort(),
          topicDefinition,
          MqttTransportProtocol.class
      ));
    } else if (isPrioritized(prioritizedProtocol, NatsTransportProtocol.class)) {
      outputGrounding.setTransportProtocol(makeTransportProtocol(
          BackendConfig.INSTANCE.getNatsHost(),
          BackendConfig.INSTANCE.getNatsPort(),
          topicDefinition,
          NatsTransportProtocol.class
      ));
    }

    outputGrounding.setTransportFormats(Collections
        .singletonList(spDataSet.getSupportedGrounding().getTransportFormats().get(0)));

    return new DataSetModificationMessage(outputGrounding, RandomStringUtils.randomAlphanumeric(10));
  }

  public static Boolean isPrioritized(SpProtocol prioritizedProtocol,
                                      Class<?> protocolClass) {
    return prioritizedProtocol.getProtocolClass().equals(protocolClass.getCanonicalName());
  }

  private static <T> T makeTransportProtocol(String hostname, int port, TopicDefinition topicDefinition,
                                             Class<?> protocolClass) {
    T tpOut = null;
    if (protocolClass.equals(KafkaTransportProtocol.class)) {
      KafkaTransportProtocol tp = new KafkaTransportProtocol();
      tp.setKafkaPort(port);
      fillTransportProtocol(tp, hostname, topicDefinition);
      tpOut = (T) tp;
    } else if (protocolClass.equals(JmsTransportProtocol.class)) {
      JmsTransportProtocol tp = new JmsTransportProtocol();
      tp.setPort(port);
      fillTransportProtocol(tp, hostname, topicDefinition);
      tpOut = (T) tp;
    } else if (protocolClass.equals(MqttTransportProtocol.class)) {
      MqttTransportProtocol tp = new MqttTransportProtocol();
      tp.setPort(port);
      fillTransportProtocol(tp, hostname, topicDefinition);
      tpOut = (T) tp;
    } else if (protocolClass.equals(NatsTransportProtocol.class)) {
      NatsTransportProtocol tp = new NatsTransportProtocol();
      tp.setPort(port);
      fillTransportProtocol(tp, hostname, topicDefinition);
      tpOut = (T) tp;
    }
    return tpOut;
  }

  private static void fillTransportProtocol(TransportProtocol protocol, String hostname,
                                            TopicDefinition topicDefinition) {
    protocol.setBrokerHostname(hostname);
    protocol.setTopicDefinition(topicDefinition);
  }
}
