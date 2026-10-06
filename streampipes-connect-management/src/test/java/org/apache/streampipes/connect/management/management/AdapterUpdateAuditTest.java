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
import org.apache.streampipes.audit.events.AuditChange;
import org.apache.streampipes.audit.events.StandardAuditEvents;
import org.apache.streampipes.audit.events.adapter.AdapterEditedDetails;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.manager.pipeline.update.DataStreamUpdateManagement;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.resource.management.AdapterResourceManager;
import org.apache.streampipes.resource.management.DataStreamResourceManager;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class AdapterUpdateAuditTest {
  @Test
  void successfulEditRecordsChangesAndPropagatesActorToRestart() throws Exception {
    verifyEdit(true, false, false, AuditOutcome.SUCCEEDED);
  }

  @Test
  void unchangedEditableFieldsDoNotProduceEditEvent() throws Exception {
    verifyEdit(false, false, false, null);
  }

  @Test
  void persistenceFailureRecordsFailedEdit() throws Exception {
    verifyEdit(true, true, false, AuditOutcome.FAILED);
  }

  @Test
  void restartFailureRecordsPartialEdit() throws Exception {
    verifyEdit(true, false, true, AuditOutcome.PARTIAL);
  }

  private void verifyEdit(boolean changed, boolean failPersistence, boolean failRestart, AuditOutcome outcome)
      throws Exception {
    var audit = mock(AuditService.class);
    var resources = mock(SpResourceManager.class);
    var adapters = mock(AdapterResourceManager.class);
    var streams = mock(DataStreamResourceManager.class);
    var storage = mock(IAdapterStorage.class);
    var master = mock(AdapterMasterManagement.class);
    when(resources.getAuditService()).thenReturn(audit);
    when(resources.manageAdapters()).thenReturn(adapters);
    when(resources.manageDataStreams()).thenReturn(streams);
    when(adapters.getDb()).thenReturn(storage);
    var before = new AdapterDescription();
    before.setName("old");
    var after = new AdapterDescription();
    after.setElementId("adapter-1");
    after.setName(changed ? "new" : "old");
    after.setRunning(true);
    when(storage.getElementById("adapter-1")).thenReturn(before);
    if (failPersistence) {
      doThrow(new AdapterException("private")).when(adapters).encryptAndUpdate(after);
    }
    if (failRestart) {
      doThrow(new AdapterException("private")).when(master).startAdapter("adapter-1", "user-1");
    }
    try (var updates = mockConstruction(DataStreamUpdateManagement.class);
         var sources = mockStatic(SourcesManagement.class)) {
      sources.when(() -> SourcesManagement.updateDataStream(any(), any())).thenReturn(new SpDataStream());
      var manager = new AdapterUpdateManagement(master, null, resources, null);
      if (failPersistence || failRestart) {
        assertThrows(AdapterException.class, () -> manager.updateAdapter(after, "user-1"));
      } else {
        manager.updateAdapter(after, "user-1");
      }
      if (outcome == null) {
        verifyNoInteractions(audit);
      } else {
        verify(audit).record(StandardAuditEvents.ADAPTER_EDIT, outcome, "user-1", "adapter-1",
            new AdapterEditedDetails(List.of(new AuditChange("name", "old", "new"))));
        verifyNoMoreInteractions(audit);
      }
      if (!failPersistence) {
        verify(master).stopAdapter("adapter-1", true, "user-1");
        verify(master).startAdapter("adapter-1", "user-1");
      }
    }
  }
}
