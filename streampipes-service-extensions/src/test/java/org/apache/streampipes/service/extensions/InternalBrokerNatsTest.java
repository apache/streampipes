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

package org.apache.streampipes.service.extensions;

import org.apache.streampipes.dataformat.SpDataFormatManager;
import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.elements.SendToBrokerAdapterSink;
import org.apache.streampipes.messaging.InternalBrokerProvider;
import org.apache.streampipes.messaging.SpProtocolManager;
import org.apache.streampipes.messaging.nats.SpNatsProtocolFactory;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.grounding.BrokerConfiguration;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfEnvironmentVariable(named = "SP_TEST_NATS_PORT", matches = "[0-9]+")
class InternalBrokerNatsTest {
  @Test
  void registeredAdapterPublishesToTwoIndependentConsumersAndCanRestart() throws Exception {
    SpProtocolManager.INSTANCE.register(new SpNatsProtocolFactory());
    InternalBrokerProvider.requireRegistration();
    var grounding = new EventGrounding();
    grounding.setTopicDefinition(new SimpleTopicDefinition("migration.test." + UUID.randomUUID()));
    assertThrows(IllegalStateException.class, () -> InternalBrokerProvider.resolve(grounding.getTransportProtocol()));
    var registrationDefaults = new BrokerConfiguration();
    registrationDefaults.setProtocolId("nats");
    registrationDefaults.setUrl("nats://127.0.0.1:" + System.getenv("SP_TEST_NATS_PORT"));
    registrationDefaults.setToken("wrong-test-token");
    assertThrows(org.apache.streampipes.commons.exceptions.SpRuntimeException.class,
        () -> InternalBrokerProvider.configure(registrationDefaults));
    assertThrows(IllegalStateException.class, () -> InternalBrokerProvider.resolve(grounding.getTransportProtocol()));
    registrationDefaults.setToken("broker-test-token");
    InternalBrokerProvider.configure(registrationDefaults);
    InternalBrokerProvider.configure(registrationDefaults);
    for (int restart = 0; restart < 2; restart++) {
      var received = new CountDownLatch(2);
      var protocol = grounding.getTransportProtocol();
      var definition = SpProtocolManager.INSTANCE.findDefinition(protocol).orElseThrow();
      var first = definition.getConsumer(protocol);
      var second = definition.getConsumer(protocol);
      var payload = Map.<String, Object>of("value", 42);
      byte[] expected = SpDataFormatManager.getFormatDefinition().fromMap(payload);
      first.connect(event -> {
        assertEquals(new String(expected, StandardCharsets.UTF_8), new String(event, StandardCharsets.UTF_8));
        received.countDown();
      });
      second.connect(event -> {
        assertEquals(new String(expected, StandardCharsets.UTF_8), new String(event, StandardCharsets.UTF_8));
        received.countDown();
      });
      var adapter = new AdapterDescription();
      adapter.setElementId("adapter-test");
      adapter.setEventGrounding(grounding);
      try (var sink = new SendToBrokerAdapterSink(adapter)) {
        sink.process(payload);
        assertTrue(received.await(5, TimeUnit.SECONDS));
      } finally {
        first.disconnect();
        second.disconnect();
      }
    }
    registrationDefaults.setUrl("nats://127.0.0.1:1");
    assertThrows(IllegalStateException.class, () -> InternalBrokerProvider.configure(registrationDefaults));
  }
}
