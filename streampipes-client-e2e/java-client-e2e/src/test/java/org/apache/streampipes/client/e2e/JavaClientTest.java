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
import org.apache.streampipes.model.connect.adapter.AdapterDescription;

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

  private static final String TEST_PREFIX = "sp-e2e-java-semantic-";
  private static final String TOPIC_PREFIX = "sp.e2e.java.semantic.topic.";
  private static final int EXPECTED_RESOURCE_COUNT = 1;
  private static final Duration ENDPOINT_READY_TIMEOUT = Duration.ofSeconds(20);

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
    // Ensure clean state before starting the test
    support.cleanupTestResources();

    String runId = UUID.randomUUID().toString().replace("-", "");
    String topicIn = TOPIC_PREFIX + "in." + runId;
    String topicOut = TOPIC_PREFIX + "out." + runId;

    // 1. Setup Resources
    AdapterDescription adapter = support.createAndStartAdapter(topicIn);
    support.createAndStartPipeline(adapter, topicIn, topicOut);

    // 2. Wait for background synchronization (Endpoint assignment)
    support.waitUntilEndpointsReady(
            EXPECTED_RESOURCE_COUNT,
            EXPECTED_RESOURCE_COUNT,
            ENDPOINT_READY_TIMEOUT
    );

    // 3. Prepare Test Data
    List<SensorEvent> inputEvents = support.buildBooleanEvents();
    Set<String> expectedEventIds = support.expectedPassedEventIds(inputEvents);

    // 4. Execution: Publish to input and consume from output via NATS
    List<Map<String, Object>> consumed = support.publishAndConsumeNats(
            topicIn,
            topicOut,
            inputEvents,
            (long) expectedEventIds.size()
    );

    // 5. Verification
    support.assertFilteredEvents(consumed, expectedEventIds);
  }
}
