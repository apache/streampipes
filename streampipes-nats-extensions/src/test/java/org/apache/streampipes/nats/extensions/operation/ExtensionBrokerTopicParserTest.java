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

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtensionBrokerTopicParserTest {

  @Test
  void shouldExtractDecodedOperationSegments() {
    String topicPrefix = "sp.extensions.request";
    String serviceId = "org.apache.streampipes.extensions.demo";
    String appId = "org.apache.streampipes.element";

    String subscriptionBaseTopic = ExtensionServiceBrokerTopics.serviceTopic(
        topicPrefix,
        serviceId,
        List.of()
    );
    String topic = ExtensionServiceBrokerTopics.serviceTopic(
        topicPrefix,
        serviceId,
        List.of("pipeline-element-assets", "data-processor", appId)
    );

    var segments = ExtensionBrokerTopicParser.extractOperationSegments(topic, subscriptionBaseTopic);

    assertEquals(List.of("pipeline-element-assets", "data-processor", appId), segments);
  }

  @Test
  void shouldExtractProviderAndTailFromOperationSegments() {
    var segments = List.of("output-schema", "data-sink", "org.apache.streampipes.sink");

    assertEquals("data-sink", ExtensionBrokerTopicParser.extractProvider(segments, "output-schema"));
    assertEquals("org.apache.streampipes.sink", ExtensionBrokerTopicParser.extractTail(segments, 2));
  }

  @Test
  void shouldReturnEmptyForMismatchingOperationOrInvalidTailIndex() {
    var segments = List.of("runtime-options", "org.apache.streampipes.adapter");

    assertEquals("", ExtensionBrokerTopicParser.extractProvider(segments, "output-schema"));
    assertEquals("", ExtensionBrokerTopicParser.extractTail(segments, 2));
    assertEquals("", ExtensionBrokerTopicParser.extractTail(segments, -1));
  }

  @Test
  void shouldDecodeLastSegment() {
    String encodedSegment = ExtensionServiceBrokerTopics.encodeTopicSegment("org.apache.streampipes.app");
    String topic = "sp.extensions.request.demo-service." + encodedSegment;

    assertEquals("org.apache.streampipes.app", ExtensionBrokerTopicParser.extractLastSegment(topic));
  }

  @Test
  void shouldReturnEmptySegmentsForTopicOutsideSubscription() {
    String subscriptionBaseTopic = "sp.extensions.request.service-a";
    String topic = "sp.extensions.request.service-b.operation";

    var segments = ExtensionBrokerTopicParser.extractOperationSegments(topic, subscriptionBaseTopic);

    assertTrue(segments.isEmpty());
  }
}
