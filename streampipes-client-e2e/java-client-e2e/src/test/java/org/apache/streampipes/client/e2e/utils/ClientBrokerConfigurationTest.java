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

package org.apache.streampipes.client.e2e.utils;

import org.apache.streampipes.messaging.SpProtocolManager;
import org.apache.streampipes.model.grounding.InternalTransportProtocol;
import org.apache.streampipes.model.grounding.NatsTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientBrokerConfigurationTest {
  @Test
  void testSupportBindsLogicalChannelsToConfiguredNatsBroker() {
    var properties = Map.of(
        "test.host", "localhost",
        "test.port", "8030",
        "test.username", "user",
        "test.apikey", "test-key",
        "test.nats.url", "nats://127.0.0.1:4422",
        "test.nats.token", "test-token");
    var previous = new HashMap<String, String>();
    properties.forEach((key, value) -> {
      previous.put(key, System.getProperty(key));
      System.setProperty(key, value);
    });
    try {
      var client = new ClientTestSupport("broker-configuration").client();
      var settings = client.getConfig().getInternalBrokerSettings();
      assertNotNull(settings);
      var protocol = assertInstanceOf(NatsTransportProtocol.class,
          settings.bind(new InternalTransportProtocol(new SimpleTopicDefinition("original.topic"), Map.of())));
      assertEquals("127.0.0.1", protocol.getBrokerHostname());
      assertEquals(4422, protocol.getPort());
      assertEquals("test-token", protocol.getToken());
      assertEquals("original.topic", protocol.getTopicDefinition().getActualTopicName());
      assertTrue(SpProtocolManager.INSTANCE.findDefinition(protocol).isPresent());
    } finally {
      previous.forEach((key, value) -> {
        if (value == null) {
          System.clearProperty(key);
        } else {
          System.setProperty(key, value);
        }
      });
    }
  }
}
