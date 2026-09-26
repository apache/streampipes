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

package org.apache.streampipes.connect.management.management;

import org.apache.streampipes.audit.api.AuditOutcome;
import org.apache.streampipes.audit.api.AuditService;
import org.apache.streampipes.audit.events.StandardAuditEvents;
import org.apache.streampipes.audit.events.adapter.AdapterAuditRecorder;
import org.apache.streampipes.audit.events.adapter.AdapterLifecycleDetails;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.prometheus.adapter.AdapterMetrics;
import org.apache.streampipes.loadbalance.LoadManager;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointGenerator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.resource.management.AdapterResourceManager;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class AdapterLifecycleAuditTest {
  private final AuditService audit = mock(AuditService.class);
  private final WorkerRestClient worker = mock(WorkerRestClient.class);
  private final IAdapterStorage storage = mock(IAdapterStorage.class);
  private final IExtensionsServiceStorage services = mock(IExtensionsServiceStorage.class);
  private final AdapterMetrics metrics = mock(AdapterMetrics.class);
  private final SpServiceRegistration service = mock(SpServiceRegistration.class);
  private final AdapterDescription adapter = new AdapterDescription();

  private AdapterMasterManagement manager() {
    var resources = mock(SpResourceManager.class);
    var adapters = mock(AdapterResourceManager.class);
    when(resources.manageAdapters()).thenReturn(adapters);
    when(adapters.getDb()).thenReturn(storage);
    when(storage.updateElement(any())).thenAnswer(invocation -> invocation.getArgument(0));
    adapter.setElementId("adapter-1");
    adapter.setName("Adapter");
    adapter.setSelectedServiceId("service-1");
    when(service.getSvcId()).thenReturn("service-1");
    when(services.findAll()).thenReturn(List.of(service));
    return new AdapterMasterManagement(resources, metrics, worker, services, null, new AdapterAuditRecorder(audit));
  }

  @Test
  void startEmitsOnlyStartAfterWorkerSuccess() throws Exception {
    var manager = manager();
    try (var locks = mockStatic(LoadManager.class);
         var endpoints = mockConstruction(ExtensionsServiceEndpointGenerator.class, (generator, context) ->
             when(generator.selectService(any(), any(), any())).thenReturn(service))) {
      manager.startAdapter(adapter, "user-1");
      verify(audit).record(StandardAuditEvents.ADAPTER_START, AuditOutcome.SUCCEEDED, "user-1", "adapter-1", null);
      verifyNoMoreInteractions(audit);
    }
  }

  @Test
  void failedStartIsRecordedAndRethrown() throws Exception {
    var manager = manager();
    doThrow(new AdapterException("private failure")).when(worker).invokeStreamAdapter(service, adapter);
    try (var locks = mockStatic(LoadManager.class);
         var endpoints = mockConstruction(ExtensionsServiceEndpointGenerator.class, (generator, context) ->
             when(generator.selectService(any(), any(), any())).thenReturn(service))) {
      assertThrows(AdapterException.class, () -> manager.startAdapter(adapter, "user-1"));
      verify(audit).record(StandardAuditEvents.ADAPTER_START, AuditOutcome.FAILED, "user-1", "adapter-1", null);
      verifyNoMoreInteractions(audit);
    }
  }

  @Test
  void successfulStopEmitsOneEvent() throws Exception {
    var manager = manager();
    try (var locks = mockStatic(LoadManager.class)) {
      manager.stopAdapter(adapter, false, "user-1");
      verify(audit).record(StandardAuditEvents.ADAPTER_STOP, AuditOutcome.SUCCEEDED,
          "user-1", "adapter-1", new AdapterLifecycleDetails(false));
      verifyNoMoreInteractions(audit);
    }
  }

  @Test
  void failedAndForcedStopsHaveDistinctOutcomes() throws Exception {
    var manager = manager();
    doThrow(new AdapterException("private failure")).when(worker).stopAdapter(service, adapter);
    try (var locks = mockStatic(LoadManager.class)) {
      assertThrows(AdapterException.class, () -> manager.stopAdapter(adapter, false, "user-1"));
      manager.stopAdapter(adapter, true, "user-1");
      verify(audit).record(StandardAuditEvents.ADAPTER_STOP, AuditOutcome.FAILED,
          "user-1", "adapter-1", new AdapterLifecycleDetails(false));
      verify(audit).record(StandardAuditEvents.ADAPTER_STOP, AuditOutcome.PARTIAL,
          "user-1", "adapter-1", new AdapterLifecycleDetails(true));
      verifyNoMoreInteractions(audit);
    }
  }
  @Test
  void cleanupFailureAfterWorkerStopIsPartial() throws Exception {
    var manager = manager();
    doThrow(new IllegalStateException("metrics failure")).when(metrics).remove("adapter-1", "Adapter");
    try (var locks = mockStatic(LoadManager.class)) {
      assertThrows(IllegalStateException.class, () -> manager.stopAdapter(adapter, false, "user-1"));
      verify(audit).record(StandardAuditEvents.ADAPTER_STOP, AuditOutcome.PARTIAL,
          "user-1", "adapter-1", new AdapterLifecycleDetails(false));
      verifyNoMoreInteractions(audit);
    }
  }
}
