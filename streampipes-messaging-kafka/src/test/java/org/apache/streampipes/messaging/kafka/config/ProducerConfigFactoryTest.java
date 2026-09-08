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

package org.apache.streampipes.messaging.kafka.config;

import org.apache.streampipes.model.grounding.KafkaTransportProtocol;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the implementation of the {@link ProducerConfigFactory} class.
 */
class ProducerConfigFactoryTest {

  private static final String BOOTSTRAP_SERVERS = "broker1:9092,broker2:9092";

  @Test
  void testBuildProperties_brokerList_isPassedToTheProducer() {
    var protocol = new KafkaTransportProtocol();
    protocol.setBootstrapServers(BOOTSTRAP_SERVERS);
    var expected = BOOTSTRAP_SERVERS;

    var actual = new ProducerConfigFactory(protocol)
        .buildProperties(List.of())
        .get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG);

    assertEquals(expected, actual);
  }

  @Test
  void testBuildProperties_brokerList_isPassedToTheConsumer() {
    var protocol = new KafkaTransportProtocol();
    protocol.setBootstrapServers(BOOTSTRAP_SERVERS);
    protocol.setGroupId("test-group");
    var expected = BOOTSTRAP_SERVERS;

    var actual = new ConsumerConfigFactory(protocol)
        .buildProperties(List.of())
        .get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG);

    assertEquals(expected, actual);
  }

  @Test
  void testBuildProperties_noBrokerList_fallsBackToHostAndPort() {
    var protocol = new KafkaTransportProtocol();
    protocol.setBrokerHostname("localhost");
    protocol.setKafkaPort(9092);
    var expected = "localhost:9092";

    var actual = new ProducerConfigFactory(protocol)
        .buildProperties(List.of())
        .get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG);

    assertEquals(expected, actual);
  }

  @Test
  void testBuildProperties_brokerListAndHostAndPort_prefersTheBrokerList() {
    var protocol = new KafkaTransportProtocol();
    protocol.setBrokerHostname("legacy");
    protocol.setKafkaPort(1234);
    protocol.setBootstrapServers(BOOTSTRAP_SERVERS);
    var expected = BOOTSTRAP_SERVERS;

    var actual = new ProducerConfigFactory(protocol)
        .buildProperties(List.of())
        .get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG);

    assertEquals(expected, actual);
  }
}
