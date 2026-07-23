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

package org.apache.streampipes.integration.adapters;

import org.apache.streampipes.extensions.api.monitoring.SpMonitoringManager;
import org.apache.streampipes.extensions.connectors.opcua.client.OpcUaClientProvider;
import org.apache.streampipes.integration.adapters.opcua.OpcUaAdapterTestHarness;
import org.apache.streampipes.integration.adapters.opcua.OpcUaAdapterTestHarness.RunningOpcUaAdapter;
import org.apache.streampipes.integration.containers.OpcUaDemoServerContainer;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Opt-in load test for OPC UA pull reads whose server-side processing exceeds the request timeout.
 */
public class OpcUaAdapterReadTimeoutReproductionTest {

  private static final String ENABLE_PROPERTY = "streampipes.opcua.heap-reproduction";
  private static final String IMAGE_PROPERTY = "streampipes.opcua.demo.image";
  private static final String DELAYED_VALUE_NODE =
      "ns=2;s=Demo.StreamPipesTestCases.ReadFailures.DelayedValue";
  private static final NodeId DELAY_ENABLED_NODE =
      NodeId.parse("ns=2;s=Demo.StreamPipesTestCases.ReadFailures.DelayEnabled");

  private static OpcUaDemoServerContainer opcUaContainer;

  @BeforeAll
  public static void startContainer() {
    Assumptions.assumeTrue(
        Boolean.getBoolean(ENABLE_PROPERTY),
        "Enable explicitly with -D" + ENABLE_PROPERTY + "=true"
    );
    String image = System.getProperty(IMAGE_PROPERTY, "opc-ua-demo-server:heap-repro");
    opcUaContainer = new OpcUaDemoServerContainer(image);
    opcUaContainer.start();
  }

  @AfterAll
  public static void stopContainer() {
    if (opcUaContainer != null) {
      opcUaContainer.stop();
    }
  }

  @Test
  public void expiresDelayedReadsAtTransportLevel() throws Exception {
    int adapterCount = Integer.getInteger("streampipes.opcua.reproduction.adapters", 25);
    int pollingIntervalMillis =
        Integer.getInteger("streampipes.opcua.reproduction.poll-interval-ms", 100);
    int durationSeconds = Integer.getInteger("streampipes.opcua.reproduction.duration-seconds", 120);

    var harness = new OpcUaAdapterTestHarness();
    var clientProvider = new OpcUaClientProvider();
    var adapters = new ArrayList<RunningOpcUaAdapter>();

    try {
      for (int i = 0; i < adapterCount; i++) {
        String adapterId = "opcua-timeout-reproduction-" + i;
        adapters.add(
            harness.startPullAdapter(
                clientProvider,
                opcUaContainer.getEndpointUrl(),
                List.of(DELAYED_VALUE_NODE),
                pollingIntervalMillis,
                adapterId
            )
        );
      }

      enableDelayedReads();
      sampleHeapAndWait(Duration.ofSeconds(durationSeconds));

      var monitoring = SpMonitoringManager.INSTANCE.getMonitoringInfo().getLogInfos();
      long adaptersWithTimeout = monitoring.entrySet().stream()
          .filter(entry -> entry.getKey().startsWith("opcua-timeout-reproduction-"))
          .filter(entry -> entry.getValue().stream().anyMatch(logEntry ->
              logEntry.getErrorMessage().getFullStackTrace().contains("Bad_Timeout")))
          .count();

      assertTrue(
          adaptersWithTimeout == adapterCount,
          () -> "Expected timeout errors for all adapters, but found "
              + adaptersWithTimeout + " of " + adapterCount
      );
    } finally {
      for (int i = adapters.size() - 1; i >= 0; i--) {
        adapters.get(i).close();
      }
    }
  }

  private void enableDelayedReads() throws Exception {
    OpcUaClient controlClient = OpcUaClient.create(
        opcUaContainer.getEndpointUrl(),
        endpoints -> endpoints.stream()
            .filter(endpoint -> endpoint.getSecurityMode() == MessageSecurityMode.None)
            .filter(endpoint -> SecurityPolicy.None.getUri().equals(endpoint.getSecurityPolicyUri()))
            .findFirst()
            .map(this::useMappedEndpoint),
        transportConfig -> { },
        clientConfig -> { }
    );
    try {
      controlClient.connect();
      var statuses = controlClient.writeValues(
          List.of(DELAY_ENABLED_NODE),
          List.of(DataValue.valueOnly(Variant.ofBoolean(true)))
      );
      assertTrue(statuses.get(0).isGood(), "Could not enable delayed reads in demo server");
    } finally {
      controlClient.disconnect();
    }
  }

  private EndpointDescription useMappedEndpoint(EndpointDescription endpoint) {
    return new EndpointDescription(
        opcUaContainer.getEndpointUrl(),
        endpoint.getServer(),
        endpoint.getServerCertificate(),
        endpoint.getSecurityMode(),
        endpoint.getSecurityPolicyUri(),
        endpoint.getUserIdentityTokens(),
        endpoint.getTransportProfileUri(),
        endpoint.getSecurityLevel()
    );
  }

  private void sampleHeapAndWait(Duration duration) throws InterruptedException {
    var memoryBean = ManagementFactory.getMemoryMXBean();
    Instant deadline = Instant.now().plus(duration);

    while (Instant.now().isBefore(deadline)) {
      var heap = memoryBean.getHeapMemoryUsage();
      System.out.printf(
          "OPC-UA heap regression: used=%d MiB, committed=%d MiB, max=%d MiB%n",
          heap.getUsed() / 1024 / 1024,
          heap.getCommitted() / 1024 / 1024,
          heap.getMax() / 1024 / 1024
      );
      Thread.sleep(1000);
    }
  }
}
