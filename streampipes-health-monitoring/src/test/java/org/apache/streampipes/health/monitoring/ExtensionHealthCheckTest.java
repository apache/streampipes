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

import org.apache.streampipes.health.monitoring.model.ActiveCoreInstances;
import org.apache.streampipes.health.monitoring.model.ActiveResources;
import org.apache.streampipes.health.monitoring.model.HealthCheckData;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExtensionHealthCheckTest {

  private ResourceProvider resourceProvider;
  private ExtensionHealthCheck check;

  @BeforeEach
  void setUp() {
    ShutdownSignal.INSTANCE.reset();
    resourceProvider = mock(ResourceProvider.class);
    when(resourceProvider.loadActiveResources())
        .thenReturn(new ActiveResources(List.of(), List.of(), List.of(), List.of()));
    when(resourceProvider.loadActiveInstances(any())).thenReturn(Map.of());
    check = new ExtensionHealthCheck(
        resourceProvider,
        mock(IExtensionsServiceStorage.class),
        mock(ExtensionServiceRequestManager.class),
        mock(SpResourceManager.class),
        List.of(),
        Duration.ofSeconds(30)
    );
  }

  @AfterEach
  void tearDown() {
    ShutdownSignal.INSTANCE.reset();
  }

  @Test
  void runsWhileServiceIsUp() {
    check.run();

    verify(resourceProvider).loadActiveResources();
  }

  @Test
  void serviceStaysUnknownForLimitedTicksThenCountsAsDown() {
    when(resourceProvider.loadActiveInstances(any()))
        .thenReturn(Map.of("svc", new ActiveCoreInstances(List.of(), Map.of())));
    var observed = new AtomicReference<HealthCheckData>();
    var neverAnswering = new ExtensionHealthCheck(
        resourceProvider,
        mock(IExtensionsServiceStorage.class),
        mock(ExtensionServiceRequestManager.class),
        mock(SpResourceManager.class),
        List.of(),
        Duration.ofSeconds(30)
    ) {
      @Override
      protected Optional<org.apache.streampipes.model.health.ExtensionInstanceHealth> checkInstances(String id) {
        return Optional.empty();
      }

      @Override
      protected int maxUnknownTicks() {
        return 2;
      }

      @Override
      protected List<HealthCheck> getBuiltInHealthChecks(HealthCheckData healthCheckData) {
        observed.set(healthCheckData);
        return List.of();
      }
    };

    neverAnswering.run();
    assertTrue(observed.get().unreachableServiceIds().contains("svc"));
    assertFalse(observed.get().activeExtensionInstances().containsKey("svc"));

    neverAnswering.run();
    assertFalse(observed.get().unreachableServiceIds().contains("svc"));
    assertTrue(observed.get().activeExtensionInstances().containsKey("svc"));
    assertEquals(0, observed.get().activeExtensionInstances().get("svc").runningPipelineElementInstanceIds().size());
  }

  @Test
  void skipsCheckOnceShutdownIsRequested() {
    ShutdownSignal.INSTANCE.requestShutdown();

    check.run();

    verify(resourceProvider, never()).loadActiveResources();
  }
}
