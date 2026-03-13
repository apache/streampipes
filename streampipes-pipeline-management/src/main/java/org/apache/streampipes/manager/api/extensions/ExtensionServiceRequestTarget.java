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

package org.apache.streampipes.manager.api.extensions;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ExtensionServiceRequestTarget(String baseUrl,
                                            String serviceId,
                                            ExtensionServiceOperationType operation,
                                            List<String> pathSegments,
                                            List<String> topicSegments) {

  public ExtensionServiceRequestTarget {
    Objects.requireNonNull(baseUrl);
    Objects.requireNonNull(serviceId);
    Objects.requireNonNull(operation);
    Objects.requireNonNull(pathSegments);
    Objects.requireNonNull(topicSegments);
    pathSegments = List.copyOf(pathSegments);
    topicSegments = List.copyOf(topicSegments);
  }

  public static ExtensionServiceRequestTarget of(String baseUrl,
                                                 String serviceId,
                                                 ExtensionServiceOperationType operation,
                                                 String... pathSegments) {
    return new ExtensionServiceRequestTarget(
        baseUrl,
        serviceId,
        operation,
        Arrays.asList(pathSegments),
        List.of(operation.name().toLowerCase(Locale.ROOT))
    );
  }

  public static ExtensionServiceRequestTarget of(String baseUrl,
                                                 String serviceId,
                                                 ExtensionServiceOperationType operation,
                                                 List<String> pathSegments,
                                                 List<String> topicSegments) {
    return new ExtensionServiceRequestTarget(baseUrl, serviceId, operation, pathSegments, topicSegments);
  }

  public String toPath() {
    var suffix = pathSegments.stream()
        .filter(Objects::nonNull)
        .map(ExtensionServiceRequestTarget::trimSlashes)
        .filter(part -> !part.isEmpty())
        .collect(Collectors.joining("/"));

    if (suffix.isEmpty()) {
      return trimTrailingSlash(baseUrl);
    }
    return trimTrailingSlash(baseUrl) + "/" + suffix;
  }

  public String toTopic(String topicPrefix) {
    var segments = topicSegments.isEmpty()
        ? List.of(operation.name().toLowerCase(Locale.ROOT))
        : topicSegments;

    return Stream.concat(
            Stream.of(topicPrefix, serviceId),
            segments.stream())
        .filter(Objects::nonNull)
        .map(ExtensionServiceRequestTarget::toTopicSegment)
        .filter(part -> !part.isEmpty())
        .collect(Collectors.joining("."));
  }

  private static String trimTrailingSlash(String value) {
    return value.replaceAll("/+$", "");
  }

  private static String trimSlashes(String value) {
    return value.replaceAll("^/+", "").replaceAll("/+$", "");
  }

  private static String toTopicSegment(String value) {
    return trimSlashes(value).replace("/", ".");
  }
}
