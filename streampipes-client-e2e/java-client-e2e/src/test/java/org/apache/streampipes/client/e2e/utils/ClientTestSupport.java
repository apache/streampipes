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
import org.apache.streampipes.client.StreamPipesCredentials;
import org.apache.streampipes.messaging.nats.SpNatsProtocolFactory;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.NatsTransportProtocol;
import org.apache.streampipes.model.message.SuccessMessage;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;

/**
 * Test support for Java client E2E: creates adapters/pipelines, publishes and consumes via NATS,
 * and provides assertions for filtered events.
 */
public final class ClientTestSupport {

  private final String testPrefix;
  private final StreamPipesClient client;

  public ClientTestSupport(String testPrefix) {
    this.testPrefix = testPrefix;
    this.client = buildClient();
  }

  public AdapterDescription createAdapter(String topicIn) {
    try {
      AdapterDescription adapter = PipelineTemplateHelper.buildAdapter(testPrefix, topicIn);
      client.adapters().create(adapter);
      return TestResourceHelper.waitForAdapter(client, adapter.getName(), Duration.ofSeconds(20));
    } catch (Exception e) {
      throw new IllegalStateException("Failed to create adapter", e);
    }
  }

  /**
   * Creates an adapter and starts it; fails the test if start fails.
   *
   * @param topicIn NATS topic the adapter will produce to
   * @return the started adapter description
   */
  public AdapterDescription createAndStartAdapter(String topicIn) {
    AdapterDescription adapter = createAdapter(topicIn);
    assertAdapterStarted(adapter);
    return adapter;
  }

  /**
   * Creates a pipeline (Boolean Filter + NATS Sink) and waits until it appears in the pipeline list.
   *
   * @param adapter  the adapter whose stream the pipeline consumes from
   * @param topicIn  NATS topic for pipeline input
   * @param topicOut NATS topic for pipeline output
   * @return the created pipeline from the backend
   */
  public Pipeline createPipeline(AdapterDescription adapter, String topicIn, String topicOut) {
    try {
      Pipeline pipeline = PipelineTemplateHelper.buildPipeline(testPrefix, adapter, topicIn, topicOut);
      client.pipelines().create(pipeline);
      return TestResourceHelper.waitForPipeline(client, pipeline.getName(), Duration.ofSeconds(20));
    } catch (Exception e) {
      throw new IllegalStateException("[pipeline.create] Failed to create pipeline", e);
    }
  }

  /**
   * Creates a pipeline and starts it; fails the test if start fails.
   *
   * @param adapter  the adapter whose stream the pipeline consumes from
   * @param topicIn  NATS topic for pipeline input
   * @param topicOut NATS topic for pipeline output
   * @return the started pipeline
   */
  public Pipeline createAndStartPipeline(AdapterDescription adapter, String topicIn, String topicOut) {
    Pipeline pipeline = createPipeline(adapter, topicIn, topicOut);
    startPipelineWithRetry(pipeline, 3, Duration.ofSeconds(5));
    return pipeline;
  }

  /**
   * Starts the pipeline with retries so that extension service discovery can complete (e.g. in Docker).
   *
   * @param pipeline   the pipeline to start
   * @param maxAttempts number of start attempts
   * @param delayBetween delay between attempts
   */
  public void startPipelineWithRetry(Pipeline pipeline, int maxAttempts, Duration delayBetween) {
    PipelineOperationStatus status = null;
    for (int attempt = 1; attempt <= maxAttempts; attempt++) {
      status = client.pipelines().start(pipeline.getPipelineId());
      if (status.isSuccess()) {
        return;
      }
      if (attempt < maxAttempts) {
        try {
          Thread.sleep(delayBetween.toMillis());
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          throw new IllegalStateException("Interrupted while waiting to retry pipeline start", e);
        }
      }
    }
    Assertions.fail("[pipeline.start] Pipeline start failed after " + maxAttempts + " attempts: "
        + pipeline.getPipelineId() + " - " + (status != null ? status.getTitle() : "unknown"));
  }

  /**
   * Starts the adapter via the client API and asserts success.
   *
   * @param adapter the adapter to start
   */
  public void assertAdapterStarted(AdapterDescription adapter) {
    SuccessMessage startStatus = client.adapters().start(adapter.getElementId());
    Assertions.assertTrue(startStatus.isSuccess(),
        "[adapter.start] Adapter start failed: " + adapter.getElementId());
  }

  /**
   * Starts the pipeline via the client API and asserts success.
   *
   * @param pipeline the pipeline to start
   */
  public void assertPipelineStarted(Pipeline pipeline) {
    PipelineOperationStatus status = client.pipelines().start(pipeline.getPipelineId());
    Assertions.assertTrue(status.isSuccess(),
        "[pipeline.start] Pipeline start failed: " + pipeline.getPipelineId() + " - " + status.getTitle());
  }

  /**
   * Builds a fixed list of sensor events (mix of true/false for boolean filter testing).
   *
   * @return list of 6 events, 3 with {@code sensor_fault_flags == true}
   */
  public List<SensorEvent> buildBooleanEvents() {
    List<SensorEvent> events = new ArrayList<>();
    events.add(new SensorEvent("event-1", 12.5, 1.2, "sensor-1", true));
    events.add(new SensorEvent("event-2", 13.0, 1.3, "sensor-2", false));
    events.add(new SensorEvent("event-3", 14.1, 1.4, "sensor-3", true));
    events.add(new SensorEvent("event-4", 15.6, 1.5, "sensor-4", false));
    events.add(new SensorEvent("event-5", 16.0, 1.6, "sensor-5", true));
    events.add(new SensorEvent("event-6", 17.3, 1.7, "sensor-6", false));
    return events;
  }

  /**
   * Returns event IDs that are expected to pass the Boolean Filter (where {@code sensor_fault_flags} is true).
   *
   * @param inputEvents events sent into the pipeline
   * @return set of event IDs that should appear in the output
   */
  public Set<String> expectedPassedEventIds(List<SensorEvent> inputEvents) {
    return inputEvents.stream()
        .filter(SensorEvent::sensorFaultFlags)
        .map(SensorEvent::eventId)
        .collect(Collectors.toSet());
  }

  /**
   * Publishes events to {@code topicIn} via the client producer and consumes from {@code topicOut}
   * until at least {@code expectedEventCount} events are received (or timeout).
   *
   * @param topicIn            NATS topic to publish to
   * @param topicOut           NATS topic to subscribe to
   * @param inputEvents        events to publish
   * @param expectedEventCount minimum number of events to wait for
   * @return list of consumed events (as raw maps)
   */
  public List<Map<String, Object>> publishAndConsumeNats(
      String topicIn,
      String topicOut,
      List<SensorEvent> inputEvents,
      long expectedEventCount) {
    NatsTransportProtocol protocolIn = natsProtocolForTopic(topicIn);
    NatsTransportProtocol protocolOut = natsProtocolForTopic(topicOut);

    List<Map<String, Object>> consumed = new ArrayList<>();
    var subscription = client.streams().subscribe(
        streamForTopic(protocolOut),
        event -> consumed.add(event.getRaw()));

    var producer = client.streams().getProducer(streamForTopic(protocolIn));
    try {
      for (SensorEvent event : inputEvents) {
        producer.publish(event.toMap());
      }
      await().atMost(Duration.ofSeconds(20))
          .until(() -> consumed.size() >= expectedEventCount);
    } finally {
      producer.close();
      subscription.unsubscribe();
    }
    return consumed;
  }

  private static SpDataStream streamForTopic(NatsTransportProtocol protocol) {
    SpDataStream stream = new SpDataStream();
    stream.setEventGrounding(new EventGrounding(protocol));
    return stream;
  }

  private static NatsTransportProtocol natsProtocolForTopic(String topic) {
    String natsUrl = System.getProperty("test.nats.url", "nats://127.0.0.1:4222");
    String host = "127.0.0.1";
    int port = 4222;
    if (natsUrl.startsWith("nats://")) {
      String rest = natsUrl.substring(7);
      int colon = rest.indexOf(':');
      if (colon > 0) {
        host = rest.substring(0, colon);
        port = Integer.parseInt(rest.substring(colon + 1));
      }
    }
    return new NatsTransportProtocol(host, port, topic);
  }

  /**
   * Asserts that consumed events contain at least the expected set of event IDs (our test events).
   * The adapter (Machine Simulator) also publishes to the same input topic, so the total
   * consumed count may exceed expectedEventIds.size(). Duplicate deliveries of our events
   * can occur (e.g. NATS or pipeline semantics), so we only assert that each of our
   * event IDs appears at least once.
   *
   * @param consumed          events received from the output topic (may include adapter traffic)
   * @param expectedEventIds  expected set of event IDs from our published events
   */
  public void assertFilteredEvents(List<Map<String, Object>> consumed, Set<String> expectedEventIds) {
    Set<String> ourEventIdsInOutput = consumed.stream()
        .map(event -> String.valueOf(event.get("eventId")))
        .filter(expectedEventIds::contains)
        .collect(Collectors.toSet());

    Assertions.assertTrue(ourEventIdsInOutput.containsAll(expectedEventIds),
        "[nats.consume] Output must contain at least our expected event IDs: expected " + expectedEventIds + ", found " + ourEventIdsInOutput + ".");
  }

  /**
   * Waits until all adapters and pipelines with the test prefix have non-blank endpoint URLs assigned.
   *
   * @param expectedAdapterCount  minimum number of adapters to wait for
   * @param expectedPipelineCount minimum number of pipelines to wait for
   * @param timeout               maximum wait duration
   */
  public void waitUntilEndpointsReady(int expectedAdapterCount,
                                      int expectedPipelineCount,
                                      Duration timeout) {
    TestResourceHelper.waitUntilEndpointsReady(client, testPrefix, expectedAdapterCount, expectedPipelineCount, timeout);
  }

  /** Stops and deletes all adapters and pipelines whose names start with the test prefix. */
  public void cleanupTestResources() {
    TestResourceHelper.cleanup(client, testPrefix);
  }

  /** Returns the StreamPipes client instance used by this support. */
  public StreamPipesClient client() {
    return client;
  }

  /**
   * Returns the selected endpoint URL of the first processor (SEPA) in the pipeline.
   *
   * @param pipeline the pipeline to inspect
   * @return endpoint URL or null if no processors
   */
  public static String extractProcessorEndpoint(Pipeline pipeline) {
    if (pipeline.getSepas() == null || pipeline.getSepas().isEmpty()) {
      return null;
    }
    DataProcessorInvocation processor = pipeline.getSepas().get(0);
    return processor.getSelectedEndpointUrl();
  }

  private StreamPipesClient buildClient() {
    String host = requiredProperty("test.host");
    int port = Integer.parseInt(requiredProperty("test.port"));
    String user = requiredProperty("test.username");
    String apiKey = requiredProperty("test.apikey");
    var client = StreamPipesClient.create(host, port, StreamPipesCredentials.withApiKey(user, apiKey), true);
    client.registerProtocol(new SpNatsProtocolFactory());
    return client;
  }

  private static String requiredProperty(String key) {
    String value = System.getProperty(key);
    if (value == null || value.isBlank()) {
      throw new IllegalStateException("Missing system property: " + key);
    }
    return value;
  }

  /**
   * Test event payload matching the Machine Simulator schema; used for publishing and assertion.
   */
  public record SensorEvent(String eventId,
                            double density,
                            double massFlow,
                            String sensorId,
                            boolean sensorFaultFlags) {
    public Map<String, Object> toMap() {
      Map<String, Object> event = new HashMap<>();
      event.put("eventId", eventId);
      event.put("density", density);
      event.put("mass_flow", massFlow);
      event.put("sensorId", sensorId);
      event.put("sensor_fault_flags", sensorFaultFlags);
      event.put("temperature", 22.5);
      event.put("timestamp", System.currentTimeMillis());
      event.put("volume_flow", 2.3);
      return event;
    }
  }
}
