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

package org.apache.streampipes.extensions.management.connect;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.management.connect.adapter.model.EventCollector;
import org.apache.streampipes.extensions.management.init.IDeclarersSingleton;
import org.apache.streampipes.extensions.management.init.RunningAdapterInstances;
import org.apache.streampipes.model.health.AdapterInstanceState;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AdapterWorkerManagementTest {

  @Test
  public void invokeAdapterNotPresent() throws AdapterException {
    var adapterDescription = AdapterConfigurationBuilder
        .create("id", 0,  null)
        .build();
    adapterDescription.setElementId("adapter-id");
    var adapterTransitionRegistry = new AdapterTransitionRegistry();

    var declarerSingleton = mock(IDeclarersSingleton.class);
    when(declarerSingleton.getAdapter(any())).thenAnswer(invocation -> {
      assertTrue(adapterTransitionRegistry
          .getTransitioningAdapterInstanceStates()
          .containsKey("adapter-id"));
      assertTrue(adapterTransitionRegistry
          .getTransitioningAdapterInstanceStates()
          .containsValue(AdapterInstanceState.STARTING));
      return Optional.empty();
    });

    var adapterWorkerManagement = new AdapterWorkerManagement(
        null, declarerSingleton, adapterTransitionRegistry);

    assertThrows(AdapterException.class, () -> adapterWorkerManagement.invokeAdapter(adapterDescription));
    assertTrue(adapterTransitionRegistry.getTransitioningAdapterInstanceStates().isEmpty());

  }

  @Test
  public void stopAdapterClosesEventCollectorWhenAdapterStopFails() throws AdapterException {
    var elementId = "adapter-id-" + UUID.randomUUID();
    var adapterDescription = AdapterConfigurationBuilder
        .create("id", 0,  null)
        .build();
    adapterDescription.setElementId(elementId);

    var adapter = mock(StreamPipesAdapter.class);
    var adapterConfig = mock(IAdapterConfiguration.class);
    var eventCollector = mock(EventCollector.class);

    when(adapter.declareConfig()).thenReturn(adapterConfig);
    when(adapterConfig.getSupportedParsers()).thenReturn(List.of());
    doThrow(new AdapterException("stop failed"))
        .when(adapter)
        .onAdapterStopped(any(), any());

    RunningAdapterInstances.INSTANCE.addAdapter(elementId, adapter, adapterDescription, eventCollector);
    var adapterWorkerManagement = new AdapterWorkerManagement(
        RunningAdapterInstances.INSTANCE, null, new AdapterTransitionRegistry()) {
      @Override
      protected IAdapterRuntimeContext makeRuntimeContext(String adapterInstanceId) {
        return mock(IAdapterRuntimeContext.class);
      }
    };

    assertThrows(AdapterException.class, () -> adapterWorkerManagement.stopAdapter(adapterDescription));

    verify(eventCollector).close();
  }
}
