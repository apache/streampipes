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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ExtensionServiceBrokerTopics {

  public static final String DEFAULT_REQUEST_TOPIC_PREFIX = "sp.extensions.request";

  public static final String TRANSPORT_TAG_HTTP = "transport:http";
  public static final String TRANSPORT_TAG_NATS = "transport:nats";

  private ExtensionServiceBrokerTopics() {
  }

  public static String serviceWildcard(String topicPrefix, String serviceId) {
    return serviceTopic(topicPrefix, serviceId, List.of()) + ".>";
  }

  public static String serviceTopic(String topicPrefix,
                                    String serviceId,
                                    List<String> topicSegments) {
    return Stream.concat(
            Stream.of(topicPrefix, serviceId),
            topicSegments.stream())
        .filter(Objects::nonNull)
        .map(ExtensionServiceBrokerTopics::normalizeSegment)
        .filter(part -> !part.isEmpty())
        .collect(Collectors.joining("."));
  }

  private static String normalizeSegment(String value) {
    return trimSlashes(value).replace("/", ".");
  }

  private static String trimSlashes(String value) {
    return value.replaceAll("^/+", "").replaceAll("/+$", "");
  }
}
