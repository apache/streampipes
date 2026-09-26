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
import org.apache.streampipes.audit.events.AdapterAuditRecorder;
import org.apache.streampipes.audit.events.AdapterCreatedDetails;
import org.apache.streampipes.audit.events.AdapterCreationReason;
import org.apache.streampipes.audit.events.StandardAuditEvents;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.prometheus.adapter.AdapterMetricsManager;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.util.GroundingUtils;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.resource.management.AdapterResourceManager;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class AdapterMasterManagementTest {

  @Test
  public void getAdapter_FailNull() {
    var adapterStorage = mock(IAdapterStorage.class);
    var adapterResourceManager = mock(AdapterResourceManager.class);
    var resourceManager = mock(SpResourceManager.class);
    var workerRestClient = mock(WorkerRestClient.class);
    var serviceStorage = mock(IExtensionsServiceStorage.class);
    var requestManager = mock(ExtensionServiceRequestManager.class);
    when(resourceManager.manageAdapters()).thenReturn(adapterResourceManager);
    when(adapterResourceManager.getDb()).thenReturn(adapterStorage);
    when(adapterStorage.getElementById("id2")).thenReturn(null);

    var adapterMasterManagement =
        new AdapterMasterManagement(
            resourceManager,
            AdapterMetricsManager.INSTANCE.getAdapterMetrics(),
            workerRestClient,
            serviceStorage,
            requestManager,
            new AdapterAuditRecorder(AuditService.disabled())
        );

    assertThrows(AdapterException.class, () -> adapterMasterManagement.getAdapter("id2"));
  }

  @Test
  public void getAdapter_Fail() {
    var adapterStorage = mock(IAdapterStorage.class);
    var adapterResourceManager = mock(AdapterResourceManager.class);
    var resourceManager = mock(SpResourceManager.class);
    var workerRestClient = mock(WorkerRestClient.class);
    var serviceStorage = mock(IExtensionsServiceStorage.class);
    var requestManager = mock(ExtensionServiceRequestManager.class);
    when(resourceManager.manageAdapters()).thenReturn(adapterResourceManager);
    when(adapterResourceManager.getDb()).thenReturn(adapterStorage);
    when(adapterStorage.getElementById("id2")).thenReturn(null);

    var adapterMasterManagement =
        new AdapterMasterManagement(
            resourceManager,
            AdapterMetricsManager.INSTANCE.getAdapterMetrics(),
            workerRestClient,
            serviceStorage,
            requestManager,
            new AdapterAuditRecorder(AuditService.disabled())
        );

    assertThrows(AdapterException.class, () -> adapterMasterManagement.getAdapter("id2"));
  }

  @Test
  public void getAllAdapters_Success() {
    var adapterDescriptions = List.of(new AdapterDescription());
    var adapterStorage = mock(IAdapterStorage.class);
    var adapterResourceManager = mock(AdapterResourceManager.class);
    var resourceManager = mock(SpResourceManager.class);
    var workerRestClient = mock(WorkerRestClient.class);
    var serviceStorage = mock(IExtensionsServiceStorage.class);
    var requestManager = mock(ExtensionServiceRequestManager.class);
    when(resourceManager.manageAdapters()).thenReturn(adapterResourceManager);
    when(adapterResourceManager.getDb()).thenReturn(adapterStorage);
    when(adapterStorage.findAll()).thenReturn(adapterDescriptions);

    AdapterMasterManagement adapterMasterManagement =
        new AdapterMasterManagement(
            resourceManager,
            AdapterMetricsManager.INSTANCE.getAdapterMetrics(),
            workerRestClient,
            serviceStorage,
            requestManager,
            new AdapterAuditRecorder(AuditService.disabled())
        );

    List<AdapterDescription> result = adapterMasterManagement.getAllAdapterInstances();

    Assertions.assertEquals(1, result.size());
  }


  @Test
  void creationEmitsOneEventAfterAdapterAndStreamAreStored() throws Exception {
    verifyCreationAudit(false, false, AuditOutcome.SUCCEEDED, null);
  }

  @Test
  void streamFailureEmitsPartialInsteadOfSuccess() throws Exception {
    verifyCreationAudit(false, true, AuditOutcome.PARTIAL, AdapterCreationReason.STREAM_CREATION_REJECTED);
  }

  @Test
  void adapterFailureEmitsFailedAndPreservesException() throws Exception {
    verifyCreationAudit(true, false, AuditOutcome.FAILED, AdapterCreationReason.ADAPTER_CREATION_FAILED);
  }

  @Test
  void thrownStreamFailureEmitsPartialAndPreservesException() throws Exception {
    verifyCreationAudit(false, false, AuditOutcome.PARTIAL, AdapterCreationReason.STREAM_CREATION_FAILED);
  }

  @Test
  void auditRecorderRequiresAnExplicitService() {
    assertThrows(NullPointerException.class, () -> new AdapterAuditRecorder(null));
  }

  private void verifyCreationAudit(boolean failAdapter, boolean failStream,
                                   AuditOutcome expected, AdapterCreationReason reason) throws Exception {
    var resources = mock(SpResourceManager.class);
    var adapters = mock(AdapterResourceManager.class);
    var audit = mock(AuditService.class);
    when(resources.manageAdapters()).thenReturn(adapters);
    var manager = spy(new AdapterMasterManagement(resources, null, null, null, null, new AdapterAuditRecorder(audit)));
    doReturn(!failStream).when(manager).createDataStreamForAdapter(any(), anyString(), anyString(), anyString());
    boolean throwsStream = reason == AdapterCreationReason.STREAM_CREATION_FAILED;
    if (throwsStream) {
      doThrow(new AdapterException("sensitive stream error")).when(manager)
          .createDataStreamForAdapter(any(), anyString(), anyString(), anyString());
    }
    var adapter = new AdapterDescription();
    if (failAdapter) {
      doThrow(new AdapterException("sensitive error")).when(adapters).encryptAndCreate(adapter);
    }
    try (var grounding = mockStatic(GroundingUtils.class)) {
      grounding.when(GroundingUtils::createEventGrounding).thenReturn(new EventGrounding());
      if (failAdapter || throwsStream) {
        assertThrows(AdapterException.class, () -> manager.addAdapter(adapter, "adapter-1", "user-1"));
      } else {
        manager.addAdapter(adapter, "adapter-1", "user-1");
      }
    }
    var details = new AdapterCreatedDetails(adapter.getCorrespondingDataStreamElementId(), reason);
    verify(audit).record(StandardAuditEvents.ADAPTER_CREATE, expected, "user-1", "adapter-1", details);
    verifyNoMoreInteractions(audit);
    if (!failAdapter) {
      var order = inOrder(adapters, manager, audit);
      order.verify(adapters).encryptAndCreate(adapter);
      order.verify(manager).createDataStreamForAdapter(eq(adapter), eq("adapter-1"), anyString(), eq("user-1"));
      order.verify(audit).record(StandardAuditEvents.ADAPTER_CREATE, expected, "user-1", "adapter-1", details);
    }
  }
}
