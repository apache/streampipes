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

package org.apache.streampipes.extensions.connectors.kafka.shared.kafka;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for the implementation of the {@link KafkaBootstrapServersParser} class.
 */
class KafkaBootstrapServersParserTest {

  @Test
  void testParse_singleBroker_returnsTheBrokerUnchanged() {
    var expected = "broker1:9092";
    var actual = KafkaBootstrapServersParser.parse("broker1:9092");
    assertEquals(expected, actual);
  }

  @Test
  void testParse_multipleBrokers_returnsAllOfThem() {
    var expected = "broker1:9092,broker2:9092,broker3:9092";
    var actual = KafkaBootstrapServersParser.parse("broker1:9092,broker2:9092,broker3:9092");
    assertEquals(expected, actual);
  }

  @Test
  void testParse_whitespaceAndEmptyEntries_returnsOnlyTheBrokers() {
    var expected = "broker1:9092,broker2:9093";
    var actual = KafkaBootstrapServersParser.parse(" broker1:9092 , , broker2:9093 ,");
    assertEquals(expected, actual);
  }

  @Test
  void testParse_duplicateBrokers_returnsEachOnceInTheGivenOrder() {
    var expected = "broker2:9092,broker1:9092";
    var actual = KafkaBootstrapServersParser.parse("broker2:9092,broker1:9092,broker2:9092");
    assertEquals(expected, actual);
  }

  @Test
  void testParse_hostnamesAndIpAddresses_returnsAllOfThem() {
    var expected = "127.0.0.1:9092,[::1]:9093,BROKER-1.example.com:9094";
    var actual = KafkaBootstrapServersParser.parse("127.0.0.1:9092,[::1]:9093,BROKER-1.example.com:9094");
    assertEquals(expected, actual);
  }

  @Test
  void testParse_brokerWithoutPort_throwsException() {
    try {
      KafkaBootstrapServersParser.parse("broker1");
      fail("No exception on #parse");
    } catch (SpRuntimeException e) {
      assertTrue(e.getMessage().contains("is not a valid Kafka broker"));
    }
  }

  @Test
  void testParse_oneOfSeveralBrokersWithoutPort_throwsException() {
    try {
      KafkaBootstrapServersParser.parse("broker1:9092,broker2");
      fail("No exception on #parse");
    } catch (SpRuntimeException e) {
      assertTrue(e.getMessage().contains("is not a valid Kafka broker"));
    }
  }

  @Test
  void testParse_brokerWithoutHost_throwsException() {
    try {
      KafkaBootstrapServersParser.parse(":9092");
      fail("No exception on #parse");
    } catch (SpRuntimeException e) {
      assertTrue(e.getMessage().contains("is not a valid Kafka broker"));
    }
  }

  @Test
  void testParse_portIsNotANumber_throwsException() {
    try {
      KafkaBootstrapServersParser.parse("broker1:port");
      fail("No exception on #parse");
    } catch (SpRuntimeException e) {
      assertTrue(e.getMessage().contains("is not a valid Kafka broker"));
    }
  }

  @Test
  void testParse_brokerWithProtocol_throwsException() {
    try {
      KafkaBootstrapServersParser.parse("kafka://broker1:9092");
      fail("No exception on #parse");
    } catch (SpRuntimeException e) {
      assertTrue(e.getMessage().contains("is not a valid Kafka broker"));
    }
  }

  @Test
  void testParse_brokerWithTrailingPath_throwsException() {
    try {
      KafkaBootstrapServersParser.parse("broker1:9092/path");
      fail("No exception on #parse");
    } catch (SpRuntimeException e) {
      assertTrue(e.getMessage().contains("is not a valid Kafka broker"));
    }
  }

  @Test
  void testParse_brokerWithAnAdditionalColon_throwsException() {
    try {
      KafkaBootstrapServersParser.parse("broker:1:9092");
      fail("No exception on #parse");
    } catch (SpRuntimeException e) {
      assertTrue(e.getMessage().contains("is not a valid Kafka broker"));
    }
  }

  @Test
  void testParse_ipv6AddressWithoutClosingBracket_throwsException() {
    try {
      KafkaBootstrapServersParser.parse("[::1:9092");
      fail("No exception on #parse");
    } catch (SpRuntimeException e) {
      assertTrue(e.getMessage().contains("is not a valid Kafka broker"));
    }
  }

  @Test
  void testParse_blankInput_throwsException() {
    try {
      KafkaBootstrapServersParser.parse("  ");
      fail("No exception on #parse");
    } catch (SpRuntimeException e) {
      assertTrue(e.getMessage().startsWith("No Kafka broker was provided"));
    }
  }

  @Test
  void testParse_nullInput_throwsException() {
    try {
      KafkaBootstrapServersParser.parse(null);
      fail("No exception on #parse");
    } catch (SpRuntimeException e) {
      assertTrue(e.getMessage().startsWith("No Kafka broker was provided"));
    }
  }

  @Test
  void testParse_onlySeparators_throwsException() {
    try {
      KafkaBootstrapServersParser.parse(",,,");
      fail("No exception on #parse");
    } catch (SpRuntimeException e) {
      assertTrue(e.getMessage().startsWith("No Kafka broker was provided"));
    }
  }
}
