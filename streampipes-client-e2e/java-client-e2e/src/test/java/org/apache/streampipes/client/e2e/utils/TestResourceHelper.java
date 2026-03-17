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

package org.apache.streampipes.client.e2e.utils;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.pipeline.Pipeline;

import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.Assertions;

import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import static org.awaitility.Awaitility.await;

/**
 * Helper for waiting on and cleaning up adapters and pipelines in E2E tests.
 */
final class TestResourceHelper {

  private static final long POLL_INTERVAL_MS = 500L;
  private static final long DEFAULT_POLL_INTERVAL_SEC = 1L;

  private TestResourceHelper() {
  }

  /**
   * Polls the adapter list until an adapter with the given name appears, or the timeout is reached.
   *
   * @param client       StreamPipes client
   * @param adapterName  name of the adapter to find
   * @param timeout      maximum wait duration
   * @return the adapter description
   * @throws IllegalStateException if not found within the timeout
   */
  static AdapterDescription waitForAdapter(StreamPipesClient client,
                                          String adapterName,
                                          Duration timeout) {
    return waitFor(
            () -> client.adapters().all().stream()
                    .filter(a -> adapterName.equals(a.getName()))
                    .findFirst()
                    .orElse(null),
            timeout,
            "Adapter not found after creation: " + adapterName
    );
  }

  /**
   * Polls the pipeline list until a pipeline with the given name appears, or the timeout is reached.
   *
   * @param client        StreamPipes client
   * @param pipelineName  name of the pipeline to find
   * @param timeout       maximum wait duration
   * @return the pipeline
   * @throws IllegalStateException if not found within the timeout
   */
  static Pipeline waitForPipeline(StreamPipesClient client,
                                  String pipelineName,
                                  Duration timeout) {
    return waitFor(
            () -> client.pipelines().all().stream()
                    .filter(p -> pipelineName.equals(p.getName()))
                    .findFirst()
                    .orElse(null),
            timeout,
            "Pipeline not found after creation: " + pipelineName
    );
  }

  /**
   * Waits until all adapters and pipelines with the given prefix have non-blank endpoint URLs.
   *
   * @param client                 StreamPipes client
   * @param testPrefix             name prefix to filter resources
   * @param expectedAdapterCount   minimum number of adapters required
   * @param expectedPipelineCount  minimum number of pipelines required
   * @param timeout                maximum wait duration
   */
  static void waitUntilEndpointsReady(StreamPipesClient client,
                                      String testPrefix,
                                      int expectedAdapterCount,
                                      int expectedPipelineCount,
                                      Duration timeout) {
    try {
      await()
              .pollInterval(Duration.ofSeconds(DEFAULT_POLL_INTERVAL_SEC))
              .atMost(timeout)
              .until(() -> {
                List<AdapterDescription> adapters = client.adapters().all().stream()
                        .filter(a -> a.getName() != null && a.getName().startsWith(testPrefix))
                        .toList();
                List<Pipeline> pipelines = client.pipelines().all().stream()
                        .filter(p -> p.getName() != null && p.getName().startsWith(testPrefix))
                        .toList();

                boolean adaptersReady = adapters.size() >= expectedAdapterCount
                        && adapters.stream().allMatch(a -> a.getSelectedEndpointUrl() != null
                        && !a.getSelectedEndpointUrl().isBlank());

                boolean pipelinesReady = pipelines.size() >= expectedPipelineCount
                        && pipelines.stream().allMatch(p -> {
                  String endpoint = ClientTestSupport.extractProcessorEndpoint(p);
                  return endpoint != null && !endpoint.isBlank();
                });

                return adaptersReady && pipelinesReady;
              });
    } catch (ConditionTimeoutException e) {
      Assertions.fail("Endpoint assignment did not finish in " + timeout.toSeconds() + " seconds");
    }
  }

  /**
   * Stops and deletes all pipelines and adapters whose names start with {@code testPrefix}.
   *
   * @param client     StreamPipes client
   * @param testPrefix name prefix to filter resources
   */
  static void cleanup(StreamPipesClient client, String testPrefix) {
    List<String> errors = new ArrayList<>();

    // Stop and delete pipelines first
    try {
      List<Pipeline> pipelines = client.pipelines().all().stream()
              .filter(p -> p.getName() != null && p.getName().startsWith(testPrefix))
              .sorted(Comparator.comparing(Pipeline::getName))
              .toList();
      for (Pipeline pipeline : pipelines) {
        capture(errors, "stop pipeline " + pipeline.getPipelineId(),
                () -> client.pipelines().stop(pipeline.getPipelineId()));
        capture(errors, "delete pipeline " + pipeline.getPipelineId(),
                () -> client.pipelines().delete(pipeline.getPipelineId()));
      }
    } catch (Exception e) {
      errors.add("scan pipelines failed: " + e.getMessage());
    }

    // Stop and delete adapters
    try {
      List<AdapterDescription> adapters = client.adapters().all().stream()
              .filter(a -> a.getName() != null && a.getName().startsWith(testPrefix))
              .sorted(Comparator.comparing(AdapterDescription::getName))
              .toList();
      for (AdapterDescription adapter : adapters) {
        capture(errors, "stop adapter " + adapter.getElementId(),
                () -> client.adapters().stop(adapter.getElementId()));
        capture(errors, "delete adapter " + adapter.getElementId(),
                () -> client.adapters().delete(adapter.getElementId()));
      }
    } catch (Exception e) {
      errors.add("scan adapters failed: " + e.getMessage());
    }

    if (!errors.isEmpty()) {
      Assertions.fail("Cleanup errors:\n" + String.join("\n", errors));
    }
  }

  private static <T> T waitFor(Supplier<T> poll, Duration timeout, String errorMessage) {
    try {
      await()
              .pollInterval(Duration.ofMillis(POLL_INTERVAL_MS))
              .atMost(timeout)
              .until(() -> poll.get() != null);
    } catch (ConditionTimeoutException e) {
      throw new IllegalStateException(errorMessage, e);
    }
    T value = poll.get();
    if (value == null) {
      throw new IllegalStateException(errorMessage);
    }
    return value;
  }

  private static void capture(List<String> errors, String operation, ThrowingRunnable action) {
    try {
      if (operation.startsWith("delete pipeline ")) {
        runWithSuppressedStderr(action);
      } else {
        action.run();
      }
    } catch (Exception e) {
      // Handle known deserialization issue in client response during deletion
      if (operation.startsWith("delete ")
              && e.getMessage() != null
              && e.getMessage().contains("Cannot construct instance of `org.apache.streampipes.model.message.Message`")) {
        return;
      }
      errors.add(operation + " failed: " + e.getMessage());
    }
  }

  private static void runWithSuppressedStderr(ThrowingRunnable action) throws Exception {
    PrintStream originalErr = System.err;
    try (PrintStream suppressedErr = new PrintStream(OutputStream.nullOutputStream())) {
      System.setErr(suppressedErr);
      action.run();
    } finally {
      System.setErr(originalErr);
    }
  }

  @FunctionalInterface
  private interface ThrowingRunnable {
    void run() throws Exception;
  }
}
