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

import org.apache.streampipes.manager.util.TopicGenerator;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.configuration.MessagingSettings;
import org.apache.streampipes.model.configuration.SpProtocol;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.NatsTransportProtocol;
import org.apache.streampipes.model.grounding.PulsarTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.List;
import java.util.Set;

public class ProtocolSelector extends GroundingSelector {

  private final String outputTopic;
  private final List<SpProtocol> prioritizedProtocols;

  private final MessagingSettings messagingSettings;

  public ProtocolSelector(NamedStreamPipesEntity source, Set<InvocableStreamPipesEntity> targets) {
    super(source, targets);
    this.outputTopic = TopicGenerator.generateRandomTopic();
    this.messagingSettings = StorageDispatcher
        .INSTANCE
        .getNoSqlStore()
        .getSpCoreConfigurationStorage()
        .get()
        .getMessagingSettings();

    this.prioritizedProtocols =
        messagingSettings.getPrioritizedProtocols();
  }

  public TransportProtocol getPreferredProtocol() {
    if (source instanceof SpDataStream) {
      return ((SpDataStream) source)
          .getEventGrounding()
          .getTransportProtocol();
    } else {
      for (SpProtocol prioritizedProtocol : prioritizedProtocols) {
        if (prioritizedProtocol.getProtocolClass().equals(KafkaTransportProtocol.class.getCanonicalName())
            && supportsProtocol(KafkaTransportProtocol.class)) {
          return kafkaTopic();
        } else if (prioritizedProtocol.getProtocolClass().equals(JmsTransportProtocol.class.getCanonicalName())
            && supportsProtocol(JmsTransportProtocol.class)) {
          return jmsTopic();
        } else if (prioritizedProtocol.getProtocolClass().equals(MqttTransportProtocol.class.getCanonicalName())
            && supportsProtocol(MqttTransportProtocol.class)) {
          return mqttTopic();
        } else if (prioritizedProtocol.getProtocolClass().equals(NatsTransportProtocol.class.getCanonicalName())
            && supportsProtocol(NatsTransportProtocol.class)) {
          return natsTopic();
        } else if (prioritizedProtocol.getProtocolClass().equals(PulsarTransportProtocol.class.getCanonicalName())
            && supportsProtocol(PulsarTransportProtocol.class)) {
          return new PulsarTransportProtocol(messagingSettings.getPulsarUrl(),
              new SimpleTopicDefinition(outputTopic));
        }
      }
    }
    return kafkaTopic();
  }

  private TransportProtocol mqttTopic() {
    return new MqttTransportProtocol(
        messagingSettings.getMqttHost(),
        messagingSettings.getMqttPort(),
        outputTopic
    );
  }

  private TransportProtocol jmsTopic() {
    return new JmsTransportProtocol(
        messagingSettings.getJmsHost(),
        messagingSettings.getJmsPort(),
        outputTopic
    );
  }

  private TransportProtocol natsTopic() {
    return new NatsTransportProtocol(
        messagingSettings.getNatsHost(),
        messagingSettings.getNatsPort(),
        outputTopic
    );
  }

  private TransportProtocol kafkaTopic() {
    return new KafkaTransportProtocol(
        messagingSettings.getKafkaHost(),
        messagingSettings.getKafkaPort(),
        outputTopic,
        messagingSettings.getZookeeperHost(),
        messagingSettings.getZookeeperPort()
    );
  }


  public <T extends TransportProtocol> boolean supportsProtocol(Class<T> protocol) {
    List<InvocableStreamPipesEntity> elements = buildInvocables();

    return elements
        .stream()
        .allMatch(e -> e
            .getSupportedGrounding()
            .getTransportProtocols()
            .stream()
            .anyMatch(protocol::isInstance));
  }
}
