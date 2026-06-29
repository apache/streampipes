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

package org.apache.streampipes.health.monitoring;

import org.apache.streampipes.health.monitoring.model.ActiveResources;
import org.apache.streampipes.health.monitoring.model.HealthCheckData;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.health.AdapterInstanceState;
import org.apache.streampipes.model.health.ExtensionInstanceHealth;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdapterHealthCheckTest {

  @Test
  public void getAdaptersToRecoverIgnoresTransitioningAdapters() {
    var adapterDescription = new AdapterDescription();
    adapterDescription.setElementId("adapter-id");
    adapterDescription.setRunning(true);

    var activeResources = new ActiveResources(
        List.of(),
        List.of(),
        List.of(adapterDescription),
        List.of(adapterDescription)
    );
    var extensionInstanceHealth = new ExtensionInstanceHealth(
        Map.of("adapter-id", AdapterInstanceState.STOPPING),
        Set.of()
    );
    var healthCheckData = new HealthCheckData(
        null,
        activeResources,
        Map.of(),
        Map.of("service-id", extensionInstanceHealth)
    );

    var adaptersToRecover = new AdapterHealthCheck(healthCheckData).getAdaptersToRecover();

    assertTrue(adaptersToRecover.isEmpty());
  }

  @Test
  public void runCheckDoesNotRestartStartingAdapters() {
    var adapterHealthCheck = new TestAdapterHealthCheck(
        healthCheckData(AdapterInstanceState.STARTING)
    );

    adapterHealthCheck.runCheck();

    assertTrue(adapterHealthCheck.recoveredAdapters.isEmpty());
  }

  @Test
  public void runCheckDoesNotRestartStoppingAdapters() {
    var adapterHealthCheck = new TestAdapterHealthCheck(
        healthCheckData(AdapterInstanceState.STOPPING)
    );

    adapterHealthCheck.runCheck();

    assertTrue(adapterHealthCheck.recoveredAdapters.isEmpty());
  }

  private HealthCheckData healthCheckData(AdapterInstanceState adapterInstanceState) {
    var adapterDescription = new AdapterDescription();
    adapterDescription.setElementId("adapter-id");
    adapterDescription.setRunning(true);

    var activeResources = new ActiveResources(
        List.of(),
        List.of(),
        List.of(adapterDescription),
        List.of(adapterDescription)
    );
    var extensionInstanceHealth = new ExtensionInstanceHealth(
        Map.of("adapter-id", adapterInstanceState),
        Set.of()
    );

    return new HealthCheckData(
        null,
        activeResources,
        Map.of(),
        Map.of("service-id", extensionInstanceHealth)
    );
  }

  private static class TestAdapterHealthCheck extends AdapterHealthCheck {

    private List<AdapterDescription> recoveredAdapters = List.of();

    TestAdapterHealthCheck(HealthCheckData healthCheckData) {
      super(healthCheckData);
    }

    @Override
    protected void updateMonitoringMetrics(List<AdapterDescription> runningAdapterDescriptions) {

    }

    @Override
    public void recoverAdapters(List<AdapterDescription> adaptersToRecover) {
      this.recoveredAdapters = adaptersToRecover;
    }
  }
}
