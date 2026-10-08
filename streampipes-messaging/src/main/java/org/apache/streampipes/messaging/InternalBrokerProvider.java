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

package org.apache.streampipes.messaging;

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.model.grounding.BrokerConfiguration;
import org.apache.streampipes.model.grounding.InternalTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.grounding.TransportProtocol;

import java.util.Map;

public final class InternalBrokerProvider {
  private static volatile InternalBrokerSettings settings;
  private static volatile boolean registrationRequired;
  private static volatile boolean restartRequired;

  private InternalBrokerProvider() {
  }

  public static void requireRegistration() {
    registrationRequired = true;
  }

  public static synchronized void configure(BrokerConfiguration configuration) {
    if (restartRequired) {
      throw new IllegalStateException("Internal broker configuration changed; restart the extension service");
    }
    var resolved = InternalBrokerSettings.resolve(configuration, Environments.getEnvironment());
    if (settings != null) {
      var previous = settings.configuration();
      var next = resolved.configuration();
      if (!java.util.Objects.equals(previous.getProtocolId(), next.getProtocolId())
          || !java.util.Objects.equals(previous.getUrl(), next.getUrl())
          || !java.util.Objects.equals(previous.getToken(), next.getToken())) {
        restartRequired = true;
        throw new IllegalStateException("Internal broker configuration changed; restart the extension service");
      }
      return;
    }
    var protocol = resolved.bind(new InternalTransportProtocol(new SimpleTopicDefinition("streampipes.health"), Map.of()));
    var definition = SpProtocolManager.INSTANCE.findDefinition(protocol)
        .orElseThrow(() -> new IllegalStateException("Selected broker provider is not installed"));
    definition.validateConnection(protocol);
    settings = resolved;
  }

  public static TransportProtocol resolve(TransportProtocol protocol) {
    if (!(protocol instanceof InternalTransportProtocol channel)) {
      return protocol;
    }
    return effectiveSettings().bind(channel);
  }

  public static BrokerConfiguration configuration() {
    return effectiveSettings().configuration();
  }

  private static InternalBrokerSettings effectiveSettings() {
    if (restartRequired) {
      throw new IllegalStateException("Internal broker configuration changed; restart the extension service");
    }
    var effective = settings;
    if (effective == null) {
      if (registrationRequired) {
        throw new IllegalStateException("Internal broker registration is not ready");
      }
      effective = InternalBrokerSettings.fromEnvironment(Environments.getEnvironment());
    }
    return effective;
  }
}
