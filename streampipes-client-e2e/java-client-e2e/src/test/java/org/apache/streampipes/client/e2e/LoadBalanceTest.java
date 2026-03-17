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
 * E2E test that verifies load balancing succeeds for adapters and pipelines.
 */
class LoadBalanceTest {

  private static final int RESOURCE_COUNT = 7;
  private static final int MIN_EXTENSION_INSTANCES = 2;
  private static final String TEST_PREFIX = "sp-e2e-java-lb-";
  private static final String EXTENSIONS_SERVICES_PATH = "api/v2/extensions-services";

  private static final Duration EXTENSION_REGISTRATION_TIMEOUT = Duration.ofSeconds(90);
  private static final Duration ENDPOINT_READY_TIMEOUT = Duration.ofSeconds(30);
  private static final Duration POLL_INTERVAL = Duration.ofSeconds(2);

  private final ClientTestSupport support = new ClientTestSupport(TEST_PREFIX);

  @AfterEach
  void cleanup() {
    support.cleanupTestResources();
  }

  /**
   * Cleans up any leftover resources, creates {@value #RESOURCE_COUNT} adapter+pipeline pairs with
   * unique topics, waits until all have endpoint URLs, then asserts count and that endpoints
   * are spread across at least {@value #MIN_EXTENSION_INSTANCES} instances.
   */
  @Test
  void testLoadBalance() {
    support.cleanupTestResources();

    // In CI, extension containers start sequentially; wait until at least 3 are registered
    // so that adapter/pipeline assignment can spread across instances.
    await()
            .pollInterval(POLL_INTERVAL)
            .atMost(EXTENSION_REGISTRATION_TIMEOUT)
            .until(() -> support.client().customRequest()
                    .getList(EXTENSIONS_SERVICES_PATH, SpServiceRegistration.class).size() >= MIN_EXTENSION_INSTANCES);

    for (int i = 0; i < RESOURCE_COUNT; i++) {
      String runId = UUID.randomUUID().toString().replace("-", "");
      String topicIn = "sp.e2e.java.lb.topic.in." + runId;
      String topicOut = "sp.e2e.java.lb.topic.out." + runId;

      AdapterDescription adapter = support.createAndStartAdapter(topicIn);
      support.createAndStartPipeline(adapter, topicIn, topicOut);
    }

    support.waitUntilEndpointsReady(RESOURCE_COUNT, RESOURCE_COUNT, ENDPOINT_READY_TIMEOUT);

    List<AdapterDescription> createdAdapters = support.client().adapters().all().stream()
            .filter(a -> a.getName() != null && a.getName().startsWith(TEST_PREFIX))
            .toList();

    List<Pipeline> createdPipelines = support.client().pipelines().all().stream()
            .filter(p -> p.getName() != null && p.getName().startsWith(TEST_PREFIX))
            .toList();

    Assertions.assertEquals(RESOURCE_COUNT, createdAdapters.size(),
            String.format("Expected %d adapters, but found %d.", RESOURCE_COUNT, createdAdapters.size()));
    Assertions.assertEquals(RESOURCE_COUNT, createdPipelines.size(),
            String.format("Expected %d pipelines, but found %d.", RESOURCE_COUNT, createdPipelines.size()));

    // Distinct endpoint URLs: each unique URL implies a different instance
    Set<String> adapterEndpoints = createdAdapters.stream()
            .map(AdapterDescription::getSelectedEndpointUrl)
            .filter(endpoint -> endpoint != null && !endpoint.isBlank())
            .collect(Collectors.toCollection(HashSet::new));

    Set<String> pipelineEndpoints = createdPipelines.stream()
            .map(ClientTestSupport::extractProcessorEndpoint)
            .filter(endpoint -> endpoint != null && !endpoint.isBlank())
            .collect(Collectors.toCollection(HashSet::new));

    Assertions.assertTrue(adapterEndpoints.size() >= MIN_EXTENSION_INSTANCES,
            String.format("Adapter load balancing failed: expected >= %d instances, but found %d. Endpoints: %s",
                    MIN_EXTENSION_INSTANCES, adapterEndpoints.size(), adapterEndpoints));

    Assertions.assertTrue(pipelineEndpoints.size() >= MIN_EXTENSION_INSTANCES,
            String.format("Pipeline load balancing failed: expected >= %d instances, but found %d. Endpoints: %s",
                    MIN_EXTENSION_INSTANCES, pipelineEndpoints.size(), pipelineEndpoints));
  }
}
