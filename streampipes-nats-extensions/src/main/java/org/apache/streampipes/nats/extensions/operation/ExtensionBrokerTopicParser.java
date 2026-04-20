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

package org.apache.streampipes.nats.extensions.operation;

import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerTopics;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ExtensionBrokerTopicParser {

  private ExtensionBrokerTopicParser() {
  }

  public static String extractLastSegment(String topic) {
    int separatorIndex = topic.lastIndexOf('.');
    if (separatorIndex < 0 || separatorIndex + 1 >= topic.length()) {
      return "";
    }

    return ExtensionServiceBrokerTopics.decodeTopicSegment(topic.substring(separatorIndex + 1));
  }

  public static String extractProvider(String topic,
                                       String subscriptionBaseTopic,
                                       String operationSegment) {
    return extractProvider(extractOperationSegments(topic, subscriptionBaseTopic), operationSegment);
  }

  public static String extractProvider(List<String> operationSegments, String operationSegment) {
    if (operationSegments.size() < 2 || !operationSegment.equals(operationSegments.get(0))) {
      return "";
    }

    return operationSegments.get(1);
  }

  public static String extractTail(List<String> operationSegments, int fromIndex) {
    if (fromIndex < 0 || operationSegments.size() <= fromIndex) {
      return "";
    }

    return operationSegments.stream()
        .skip(fromIndex)
        .collect(Collectors.joining("."));
  }

  public static List<String> extractOperationSegments(String topic, String subscriptionBaseTopic) {
    if (isBlank(topic) || isBlank(subscriptionBaseTopic)) {
      return List.of();
    }

    if (topic.equals(subscriptionBaseTopic)) {
      return List.of();
    }

    var prefix = subscriptionBaseTopic + ".";
    if (!topic.startsWith(prefix)) {
      return List.of();
    }

    var suffix = topic.substring(prefix.length());
    if (isBlank(suffix)) {
      return List.of();
    }

    return Arrays.stream(suffix.split("\\."))
        .map(ExtensionServiceBrokerTopics::decodeTopicSegment)
        .toList();
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
