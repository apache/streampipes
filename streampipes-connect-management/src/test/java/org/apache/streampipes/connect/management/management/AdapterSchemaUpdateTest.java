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

import org.apache.streampipes.audit.api.AuditService;
import org.apache.streampipes.manager.pipeline.update.DataStreamSchemaUpdatedEvent;
import org.apache.streampipes.manager.pipeline.update.PipelineUpdateCoordinator;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.resource.management.AdapterResourceManager;
import org.apache.streampipes.resource.management.DataStreamResourceManager;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AdapterSchemaUpdateTest {

  @Test
  void publishesSchemaEventWhenAdapterFieldAdded() throws Exception {
    verifyUpdate(true);
  }

  @Test
  void doesNotPublishSchemaEventWhenAdapterRenamed() throws Exception {
    verifyUpdate(false);
  }

  private void verifyUpdate(boolean schemaChanged) throws Exception {
    var resources = mock(SpResourceManager.class);
    var adapters = mock(AdapterResourceManager.class);
    var streams = mock(DataStreamResourceManager.class);
    var storage = mock(IAdapterStorage.class);
    var publisher = mock(ApplicationEventPublisher.class);
    when(resources.getAuditService()).thenReturn(mock(AuditService.class));
    when(resources.manageAdapters()).thenReturn(adapters);
    when(resources.manageDataStreams()).thenReturn(streams);
    when(adapters.getDb()).thenReturn(storage);
    when(storage.getElementById("adapter-1")).thenReturn(new AdapterDescription());
    var previous = new SpDataStream();
    previous.setElementId("stream-1");
    when(streams.find("stream-1")).thenReturn(previous);
    var updated = new AdapterDescription();
    updated.setElementId("adapter-1");
    updated.setCorrespondingDataStreamElementId("stream-1");
    updated.setName("Renamed adapter");
    var adapterStream = new SpDataStream(previous);
    if (schemaChanged) {
      adapterStream.getEventSchema().addEventProperty(new EventPropertyPrimitive("xs:double", "value", null, null));
    }
    updated.setDataStream(adapterStream);

    try (var ignored = mockConstruction(PipelineUpdateCoordinator.class)) {
      new AdapterUpdateManagement(mock(AdapterMasterManagement.class), null, resources, publisher)
          .updateAdapter(updated, "user-1");

      verify(streams).update(any(SpDataStream.class));
      assertEquals(0, previous.getEventSchema().getEventProperties().size());
      if (schemaChanged) {
        verify(publisher).publishEvent(any(DataStreamSchemaUpdatedEvent.class));
      } else {
        verifyNoInteractions(publisher);
      }
    }
  }
}
