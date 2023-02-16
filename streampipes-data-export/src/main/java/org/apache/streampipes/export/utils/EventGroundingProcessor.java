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

package org.apache.streampipes.export.utils;

import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.model.config.SpProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.NatsTransportProtocol;
import org.apache.streampipes.model.grounding.TransportProtocol;

public class EventGroundingProcessor {

  SpProtocol configuredProtocol;
  public EventGroundingProcessor() {
    this.configuredProtocol = BackendConfig.INSTANCE.getMessagingSettings().getPrioritizedProtocols().get(0);
  }

  public TransportProtocol applyOverride(TransportProtocol protocol) {
    var protocolOverride = getConfiguredTransportProtocol();
    protocolOverride.setTopicDefinition(protocol.getTopicDefinition());
    return protocolOverride;
  }

  private TransportProtocol getConfiguredTransportProtocol() {
    return initializeProtocol(configuredProtocol);
  }

  private TransportProtocol initializeProtocol(SpProtocol configuredProtocol) {
    if (isProtocol(configuredProtocol, KafkaTransportProtocol.class)) {
      return makeKafkaProtocol();
    } else if (isProtocol(configuredProtocol, MqttTransportProtocol.class)) {
      return makeMqttProtocol();
    } else {
      return makeNatsProtocol();
    }
  }

  private boolean isProtocol(SpProtocol configuredProtocol,
                             Class<?> actualProtocolClass) {
    return configuredProtocol.getProtocolClass().equals(actualProtocolClass.getCanonicalName());
  }


  private KafkaTransportProtocol makeKafkaProtocol() {
    var protocol = new KafkaTransportProtocol();
    protocol.setBrokerHostname(BackendConfig.INSTANCE.getKafkaHost());
    protocol.setKafkaPort(BackendConfig.INSTANCE.getKafkaPort());
    return protocol;
  }

  private MqttTransportProtocol makeMqttProtocol() {
    var protocol = new MqttTransportProtocol();
    protocol.setBrokerHostname(BackendConfig.INSTANCE.getMqttHost());
    protocol.setPort(BackendConfig.INSTANCE.getMqttPort());
    return protocol;
  }

  private NatsTransportProtocol makeNatsProtocol() {
    var protocol = new NatsTransportProtocol();
    protocol.setBrokerHostname(BackendConfig.INSTANCE.getNatsHost());
    protocol.setPort(BackendConfig.INSTANCE.getNatsPort());
    return protocol;
  }


}
