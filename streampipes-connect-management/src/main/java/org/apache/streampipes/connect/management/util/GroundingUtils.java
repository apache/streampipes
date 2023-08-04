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

package org.apache.streampipes.connect.management.util;

import org.apache.streampipes.model.configuration.SpProtocol;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.NatsTransportProtocol;
import org.apache.streampipes.model.grounding.PulsarTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.grounding.TopicDefinition;
import org.apache.streampipes.model.grounding.TransportProtocol;

import java.util.Collections;
import java.util.UUID;

public class GroundingUtils {

  private static final String TOPIC_PREFIX = "org.apache.streampipes.connect.";

  public static EventGrounding createEventGrounding() {
    EventGrounding eventGrounding = new EventGrounding();
    var messagingSettings = Utils
        .getCoreConfigStorage()
        .get()
        .getMessagingSettings();

    String topic = TOPIC_PREFIX + UUID.randomUUID().toString();
    TopicDefinition topicDefinition = new SimpleTopicDefinition(topic);

    SpProtocol prioritizedProtocol =
        messagingSettings.getPrioritizedProtocols().get(0);

    if (isPrioritized(prioritizedProtocol, JmsTransportProtocol.class)) {
      eventGrounding.setTransportProtocol(
          makeJmsTransportProtocol(
              messagingSettings.getJmsHost(),
              messagingSettings.getJmsPort(),
              topicDefinition));
    } else if (isPrioritized(prioritizedProtocol, KafkaTransportProtocol.class)) {
      eventGrounding.setTransportProtocol(
          makeKafkaTransportProtocol(
              messagingSettings.getKafkaHost(),
              messagingSettings.getKafkaPort(),
              topicDefinition));
    } else if (isPrioritized(prioritizedProtocol, MqttTransportProtocol.class)) {
      eventGrounding.setTransportProtocol(
          makeMqttTransportProtocol(
              messagingSettings.getMqttHost(),
              messagingSettings.getMqttPort(),
              topicDefinition));
    } else if (isPrioritized(prioritizedProtocol, NatsTransportProtocol.class)) {
      eventGrounding.setTransportProtocol(
          makeNatsTransportProtocol(
              messagingSettings.getNatsHost(),
              messagingSettings.getNatsPort(),
              topicDefinition));
    } else if (isPrioritized(prioritizedProtocol, PulsarTransportProtocol.class)) {
      eventGrounding.setTransportProtocol(
          makePulsarTransportProtocol(
              messagingSettings.getPulsarUrl(),
              topicDefinition
          )
      );
    }

    eventGrounding.setTransportFormats(Collections
        .singletonList(TransportFormatGenerator.getTransportFormat()));

    return eventGrounding;
  }

  private static JmsTransportProtocol makeJmsTransportProtocol(String hostname, Integer port,
                                                               TopicDefinition topicDefinition) {
    JmsTransportProtocol transportProtocol = new JmsTransportProtocol();
    transportProtocol.setPort(port);
    fillTransportProtocol(transportProtocol, hostname, topicDefinition);

    return transportProtocol;
  }

  private static MqttTransportProtocol makeMqttTransportProtocol(String hostname, Integer port,
                                                                 TopicDefinition topicDefinition) {
    MqttTransportProtocol transportProtocol = new MqttTransportProtocol();
    transportProtocol.setPort(port);
    fillTransportProtocol(transportProtocol, hostname, topicDefinition);

    return transportProtocol;
  }

  private static NatsTransportProtocol makeNatsTransportProtocol(String hostname,
                                                                 int port,
                                                                 TopicDefinition topicDefinition) {
    var tp = new NatsTransportProtocol();
    tp.setPort(port);
    fillTransportProtocol(tp, hostname, topicDefinition);

    return tp;
  }

  private static KafkaTransportProtocol makeKafkaTransportProtocol(String hostname, Integer port,
                                                                   TopicDefinition topicDefinition) {
    KafkaTransportProtocol transportProtocol = new KafkaTransportProtocol();
    transportProtocol.setKafkaPort(port);
    fillTransportProtocol(transportProtocol, hostname, topicDefinition);

    return transportProtocol;
  }

  private static PulsarTransportProtocol makePulsarTransportProtocol(String url,
                                                                     TopicDefinition topicDefinition) {

    return new PulsarTransportProtocol(url, topicDefinition);
  }

  private static void fillTransportProtocol(TransportProtocol protocol, String hostname,
                                            TopicDefinition topicDefinition) {
    protocol.setBrokerHostname(hostname);
    protocol.setTopicDefinition(topicDefinition);
  }

  public static Boolean isPrioritized(SpProtocol prioritizedProtocol,
                                      Class<?> protocolClass) {
    return prioritizedProtocol.getProtocolClass().equals(protocolClass.getCanonicalName());
  }
}
