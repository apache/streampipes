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

package org.apache.streampipes.manager.util;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.NatsTransportProtocol;
import org.apache.streampipes.model.grounding.PulsarTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.grounding.TopicDefinition;
import org.apache.streampipes.model.grounding.TransportProtocol;

import java.util.UUID;

public class GroundingUtils {

  private static final String TOPIC_PREFIX = "org.apache.streampipes.connect.";

  public static EventGrounding createEventGrounding() {
    var env = Environments.getEnvironment();
    EventGrounding eventGrounding = new EventGrounding();
    String topic = TOPIC_PREFIX + UUID.randomUUID().toString();

    var prioritizedProtocol = env.getPrioritizedProtocol().getValueOrDefault();

    eventGrounding.setTransportProtocol(makeProtocol(env, prioritizedProtocol, topic));

    return eventGrounding;
  }

  public static TransportProtocol makeProtocol(Environment env,
                                               String prioritizedProtocol,
                                               String topic) {
    var topicDefinition = new SimpleTopicDefinition(topic);
    switch (prioritizedProtocol) {
      case KafkaTransportProtocol.BROKER_ID -> {
        return makeKafkaTransportProtocol(
            env.getKafkaHost().getValueOrDefault(),
            env.getKafkaPort().getValueOrDefault(),
            topicDefinition);
      }
      case MqttTransportProtocol.BROKER_ID -> {
        return makeMqttTransportProtocol(
            env.getMqttHost().getValueOrDefault(),
            env.getMqttPort().getValueOrDefault(),
            topicDefinition);
      }
      case NatsTransportProtocol.BROKER_ID -> {
        return makeNatsTransportProtocol(
            env.getNatsHost().getValueOrDefault(),
            env.getNatsPort().getValueOrDefault(),
            topicDefinition);
      }
      case PulsarTransportProtocol.BROKER_ID -> {
        return makePulsarTransportProtocol(
            env.getPulsarUrl().getValueOrDefault(),
            topicDefinition);
      }
    }
    return null;
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
}
