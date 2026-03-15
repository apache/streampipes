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

package org.apache.streampipes.model.extensions.transport;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ExtensionServiceBrokerTopics {

  public static final String DEFAULT_REQUEST_TOPIC_PREFIX = "sp.extensions.request";

  public static final String TRANSPORT_TAG_HTTP = "transport:http";
  public static final String TRANSPORT_TAG_NATS = "transport:nats";
  private static final char ESCAPE_CHAR = '~';

  private ExtensionServiceBrokerTopics() {
  }

  public static String serviceWildcard(String topicPrefix, String serviceId) {
    return serviceTopic(topicPrefix, serviceId, List.of()) + ".>";
  }

  public static String serviceTopic(String topicPrefix,
                                    String serviceId,
                                    List<String> topicSegments) {
    return Stream.concat(
            Stream.of(normalizePrefix(topicPrefix), normalizeServiceId(serviceId)),
            topicSegments.stream().map(ExtensionServiceBrokerTopics::encodeTopicSegment))
        .filter(Objects::nonNull)
        .filter(part -> !part.isEmpty())
        .collect(Collectors.joining("."));
  }

  public static String encodeTopicSegment(String value) {
    String normalized = normalizeTopicSegment(value);
    if (normalized.isEmpty()) {
      return normalized;
    }

    StringBuilder encoded = new StringBuilder();
    byte[] bytes = normalized.getBytes(StandardCharsets.UTF_8);
    for (byte valueByte : bytes) {
      int current = valueByte & 0xFF;
      if (isSafeTopicByte(current)) {
        encoded.append((char) current);
      } else {
        encoded.append(ESCAPE_CHAR);
        encoded.append(toUpperHexChar((current >>> 4) & 0x0F));
        encoded.append(toUpperHexChar(current & 0x0F));
      }
    }

    return encoded.toString();
  }

  public static String decodeTopicSegment(String value) {
    if (value == null || value.isEmpty()) {
      return value;
    }

    ByteArrayOutputStream decoded = new ByteArrayOutputStream();
    int index = 0;
    while (index < value.length()) {
      char current = value.charAt(index);
      if (current == ESCAPE_CHAR) {
        if (index + 2 >= value.length()) {
          return value;
        }

        int high = hexValue(value.charAt(index + 1));
        int low = hexValue(value.charAt(index + 2));
        if (high < 0 || low < 0) {
          return value;
        }

        decoded.write((high << 4) + low);
        index += 3;
      } else {
        if (current > 0x7F) {
          return value;
        }

        decoded.write((byte) current);
        index++;
      }
    }

    return new String(decoded.toByteArray(), StandardCharsets.UTF_8);
  }

  private static String normalizePrefix(String value) {
    return trimSlashes(value).replace("/", ".");
  }

  private static String normalizeTopicSegment(String value) {
    if (value == null) {
      return "";
    }

    return trimSlashes(value);
  }

  private static String normalizeServiceId(String serviceId) {
    return normalizeTopicSegment(serviceId);
  }

  private static boolean isSafeTopicByte(int value) {
    return value >= 'A' && value <= 'Z'
        || value >= 'a' && value <= 'z'
        || value >= '0' && value <= '9'
        || value == '-'
        || value == '_';
  }

  private static int hexValue(char value) {
    if (value >= '0' && value <= '9') {
      return value - '0';
    }

    if (value >= 'a' && value <= 'f') {
      return value - 'a' + 10;
    }

    if (value >= 'A' && value <= 'F') {
      return value - 'A' + 10;
    }

    return -1;
  }

  private static char toUpperHexChar(int value) {
    return Character.toUpperCase(Character.forDigit(value, 16));
  }

  private static String trimSlashes(String value) {
    return value.replaceAll("^/+", "").replaceAll("/+$", "");
  }
}
