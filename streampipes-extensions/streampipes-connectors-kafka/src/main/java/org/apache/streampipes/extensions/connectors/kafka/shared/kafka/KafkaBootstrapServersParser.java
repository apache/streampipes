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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Parses and validates a user-provided broker list in the standard Kafka {@code bootstrap.servers}
 * format, e.g. {@code broker1:9092,broker2:9092}.
 */
public class KafkaBootstrapServersParser {

  private static final String ADDRESS_PREFIX = "//";
  private static final String BROKER_SEPARATOR = ",";
  private static final String PORT_SEPARATOR = ":";

  private KafkaBootstrapServersParser() {}

  /**
   * Normalize a broker list by dropping surrounding whitespace, empty entries, and duplicates.
   * <p>
   * For example: {@code " broker1:9092 , , broker2:9093,broker1:9092"} becomes
   * {@code "broker1:9092,broker2:9093"}.
   *
   * @param bootstrapServers the user input, holding one or more brokers as {@code host:port},
   *                         separated by commas.
   * @return the validated brokers in the same order, ready to be passed to a Kafka client.
   */
  public static String parse(String bootstrapServers) {
    var brokers = Arrays.stream(Objects.requireNonNullElse(bootstrapServers, "").split(BROKER_SEPARATOR))
        .map(String::trim)
        .filter(broker -> !broker.isEmpty())
        .map(KafkaBootstrapServersParser::validateBroker)
        .collect(Collectors.toCollection(LinkedHashSet::new));

    if (brokers.isEmpty()) {
      throw new SpRuntimeException(
          "No Kafka broker was provided. Expected at least one entry as host:port, e.g. broker1:9092,broker2:9092");
    }

    return String.join(BROKER_SEPARATOR, brokers);
  }

  /**
   * Ensure that a broker is written in a form a Kafka client can use.
   * Throw an exception, otherwise.
   *
   * @param broker a single broker to validate.
   * @return a validated broker.
   */
  private static String validateBroker(String broker) {
    if (!isValidBroker(broker)) {
      throw new SpRuntimeException(
          "'" + broker + "' is not a valid Kafka broker. Expected host:port, with multiple brokers "
              + "separated by a comma, e.g. broker1:9092,broker2:9092");
    }

    return broker;
  }

  /**
   * Check whether a broker is written as a host and a port, with nothing else around them.
   *
   * @param broker a single broker to check.
   * @return {@code true} if the broker consists of a host and a port.
   * Otherwise, {@code false}.
   */
  private static boolean isValidBroker(String broker) {
    try {
      var address = new URI(ADDRESS_PREFIX + broker);
      return broker.equals(address.getHost() + PORT_SEPARATOR + address.getPort());
    } catch (URISyntaxException e) {
      return false;
    }
  }
}
