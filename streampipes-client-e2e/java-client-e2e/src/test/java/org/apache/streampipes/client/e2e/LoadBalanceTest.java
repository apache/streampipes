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
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.pipeline.Pipeline;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;

/**
 * E2E test that verifies adapters and pipelines are load-balanced across at least 3 instances.
 * Creates multiple adapter+pipeline pairs, waits for endpoint assignment, then asserts that
 * distinct endpoint URLs span at least 3 instances.
 */
class LoadBalanceTest {

  /** Number of adapter+pipeline pairs to create. */
  private static final int RESOURCE_COUNT = 7;
  /** Name prefix for created adapters and pipelines (used for filtering and cleanup). */
  private static final String TEST_PREFIX = "sp-e2e-java-lb-";

  private final ClientTestSupport support = new ClientTestSupport(TEST_PREFIX);

  @AfterEach
  void cleanup() {
    support.cleanupTestResources();
  }

  /** Minimum number of extension instances that must be registered before creating adapters (CI starts them sequentially). */
  private static final int MIN_EXTENSION_INSTANCES = 3;
  /** Max time to wait for enough extension instances to register (e.g. in Docker/CI). */
  private static final Duration EXTENSION_REGISTRATION_TIMEOUT = Duration.ofSeconds(90);

  /**
   * Cleans up any leftover resources, creates {@value #RESOURCE_COUNT} adapter+pipeline pairs with
   * unique topics, waits until all have endpoint URLs, then asserts count and that endpoints
   * are spread across at least 3 instances.
   */
  @Test
  void testLoadBalance() {
    support.cleanupTestResources();

    // In CI, extension containers start sequentially; wait until at least 3 are registered
    // so that adapter/pipeline assignment can spread across instances.
    await()
        .pollInterval(Duration.ofSeconds(2))
        .atMost(EXTENSION_REGISTRATION_TIMEOUT)
        .until(() -> support.client().customRequest()
            .getList("api/v2/extensions-services", SpServiceRegistration.class).size() >= MIN_EXTENSION_INSTANCES);

    for (int i = 0; i < RESOURCE_COUNT; i++) {
      String runId = UUID.randomUUID().toString().replace("-", "");
      String topicIn = "sp.e2e.java.lb.topic.in." + runId;
      String topicOut = "sp.e2e.java.lb.topic.out." + runId;

      AdapterDescription adapter = support.createAndStartAdapter(topicIn);
      support.createAndStartPipeline(adapter, topicIn, topicOut);
    }

    support.waitUntilEndpointsReady(RESOURCE_COUNT, RESOURCE_COUNT, Duration.ofSeconds(30));

    List<AdapterDescription> createdAdapters = support.client().adapters().all().stream()
        .filter(a -> a.getName() != null && a.getName().startsWith(TEST_PREFIX))
        .collect(Collectors.toList());
    List<Pipeline> createdPipelines = support.client().pipelines().all().stream()
        .filter(p -> p.getName() != null && p.getName().startsWith(TEST_PREFIX))
        .collect(Collectors.toList());

    Assertions.assertEquals(RESOURCE_COUNT, createdAdapters.size(),
        "Expected " + RESOURCE_COUNT + " adapters, but found " + createdAdapters.size() + ".");
    Assertions.assertEquals(RESOURCE_COUNT, createdPipelines.size(),
        "Expected " + RESOURCE_COUNT + " pipelines, but found " + createdPipelines.size() + ".");

    // Distinct endpoint URLs: each unique URL implies a different instance
    Set<String> adapterEndpoints = createdAdapters.stream()
        .map(AdapterDescription::getSelectedEndpointUrl)
        .filter(endpoint -> endpoint != null && !endpoint.isBlank())
        .collect(Collectors.toCollection(HashSet::new));

    Set<String> pipelineEndpoints = createdPipelines.stream()
        .map(ClientTestSupport::extractProcessorEndpoint)
        .filter(endpoint -> endpoint != null && !endpoint.isBlank())
        .collect(Collectors.toCollection(HashSet::new));

    Assertions.assertTrue(adapterEndpoints.size() >= 3,
        "Load balancing for adapters did not spread across 3 instances.");
    Assertions.assertTrue(pipelineEndpoints.size() >= 3,
        "Load balancing for pipelines did not spread across 3 instances.");
  }
}

