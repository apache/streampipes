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

package org.apache.streampipes.health.monitoring;

import org.apache.streampipes.commons.environment.DefaultEnvironment;
import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.variable.IntEnvironmentVariable;
import org.apache.streampipes.commons.environment.variable.StringEnvironmentVariable;
import org.apache.streampipes.messaging.InternalBrokerProvider;
import org.apache.streampipes.messaging.InternalBrokerSettings;
import org.apache.streampipes.model.grounding.InternalTransportProtocol;
import org.apache.streampipes.model.grounding.NatsTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InternalBrokerSettingsTest {
  @Test
  void absentLocalOverridesInheritRegistrationInsteadOfBuiltInDefaults() {
    var defaults = new InternalBrokerSettings("nats", "nats://core-broker:4321", "server-token");
    var effective = InternalBrokerSettings.resolve(defaults.configuration(), new DefaultEnvironment());
    assertEquals("nats://core-broker:4321", effective.configuration().getUrl());
    assertEquals("server-token", effective.configuration().getToken());
  }

  @Test
  void explicitHostAndAuthenticationOverrideKeepServerPortAndProtocol() {
    var env = mock(Environment.class);
    var host = mock(StringEnvironmentVariable.class);
    var port = mock(IntEnvironmentVariable.class);
    var token = mock(StringEnvironmentVariable.class);
    when(env.getNatsHost()).thenReturn(host);
    when(env.getNatsPort()).thenReturn(port);
    when(env.getNatsToken()).thenReturn(token);
    when(host.getValueOrReturn("core-broker")).thenReturn("edge-broker");
    when(port.getValueOrReturn(4321)).thenReturn(4321);
    when(token.getValueOrReturn("server-token")).thenReturn("edge-token");
    var effective = InternalBrokerSettings.resolve(
        new InternalBrokerSettings("nats", "nats://core-broker:4321", "server-token").configuration(), env);
    var topic = new SimpleTopicDefinition("unchanged");
    var bound = (NatsTransportProtocol) effective.bind(new InternalTransportProtocol(topic, Map.of()));
    assertEquals("edge-broker", bound.getBrokerHostname());
    assertEquals(4321, bound.getPort());
    assertEquals("edge-token", bound.getToken());
    assertSame(topic, bound.getTopicDefinition());
  }

  @Test
  void preservesPulsarTlsEndpoint() {
    var settings = new InternalBrokerSettings("pulsar", "pulsar+ssl://broker:6651", null);
    var bound = settings.bind(new InternalTransportProtocol(new SimpleTopicDefinition("topic"), Map.of()));
    assertEquals("pulsar+ssl://broker:6651", bound.getBrokerHostname());
  }

  @Test
  void externalConnectionsRemainIndependent() {
    var external = new NatsTransportProtocol();
    external.setBrokerHostname("external");
    assertSame(external, InternalBrokerProvider.resolve(external));
  }

  @Test
  void invalidUrlsAndProtocolsFailWithoutIncludingCredentials() {
    for (var url : new String[]{"nats://user:secret@host:4222", "nats://host", "http://host:4222", ""}) {
      var exception = assertThrows(IllegalArgumentException.class,
          () -> new InternalBrokerSettings("nats", url, null));
      org.junit.jupiter.api.Assertions.assertFalse(exception.getMessage().contains("secret"));
    }
    assertThrows(IllegalArgumentException.class, () -> new InternalBrokerSettings("unknown", "nats://host:4222", null));
  }
}
