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

package org.apache.streampipes.client.e2e;

import org.apache.streampipes.client.e2e.utils.ClientTestSupport;
import org.apache.streampipes.client.e2e.utils.ClientTestSupport.SensorEvent;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * E2E test for the Java client: creates a Machine Simulator adapter and a pipeline (Boolean Filter
 * + NATS sink), publishes events to the input topic, consumes from the output topic, and asserts
 * that only events with {@code sensor_fault_flags == true} are received.
 */
class JavaClientTest {

  /** Name prefix for created adapters and pipelines (used for filtering and cleanup). */
  private static final String TEST_PREFIX = "sp-e2e-java-semantic-";

  private final ClientTestSupport support = new ClientTestSupport(TEST_PREFIX);

  @AfterEach
  void cleanup() {
    support.cleanupTestResources();
  }

  /**
   * Cleans up any leftover resources, creates one adapter and one pipeline with unique topics,
   * waits for endpoint assignment, publishes 6 events (3 expected to pass the filter), consumes
   * from the output topic, and asserts that exactly the 3 filtered event IDs are received.
   */
  @Test
  void testJavaClient() {
    support.cleanupTestResources();

    var runId = UUID.randomUUID().toString().replace("-", "");
    var topicIn = "sp.e2e.java.semantic.topic.in." + runId;
    var topicOut = "sp.e2e.java.semantic.topic.out." + runId;

    var adapter = support.createAndStartAdapter(topicIn);
    support.createAndStartPipeline(adapter, topicIn, topicOut);
    support.waitUntilEndpointsReady(1, 1, Duration.ofSeconds(20));

    List<SensorEvent> inputEvents = support.buildBooleanEvents();
    Set<String> expectedEventIds = support.expectedPassedEventIds(inputEvents);

    List<Map<String, Object>> consumed = support.publishAndConsumeNats(
        topicIn, topicOut, inputEvents, expectedEventIds.size());
    support.assertFilteredEvents(consumed, expectedEventIds);
  }
}

