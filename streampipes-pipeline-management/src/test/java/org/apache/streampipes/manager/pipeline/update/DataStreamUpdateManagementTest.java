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

package org.apache.streampipes.manager.pipeline.update;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.resource.management.DataStreamResourceManager;
import org.apache.streampipes.resource.management.SpResourceManager;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class DataStreamUpdateManagementTest {

  @Test
  void publishesSchemaEventAfterSuccessfulUpdate() {
    var previous = stream();
    var updated = new SpDataStream(previous);
    updated.getEventSchema().addEventProperty(new EventPropertyPrimitive("xs:string", "new-field", null, null));
    var resources = mock(SpResourceManager.class);
    var streams = mock(DataStreamResourceManager.class);
    var publisher = mock(ApplicationEventPublisher.class);
    when(resources.manageDataStreams()).thenReturn(streams);
    when(streams.find("stream-1")).thenReturn(previous);

    try (var coordinators = mockConstruction(PipelineUpdateCoordinator.class)) {
      var management = new DataStreamUpdateManagement(null, resources, publisher);
      management.updateDataStream(updated);

      var order = inOrder(streams, coordinators.constructed().getFirst(), publisher);
      order.verify(streams).find("stream-1");
      order.verify(streams).update(updated);
      order.verify(coordinators.constructed().getFirst()).updatePipelines(updated);
      order.verify(publisher).publishEvent(new DataStreamSchemaUpdatedEvent(updated.getElementId(), updated.getName()));
    }
  }

  @Test
  void persistsStreamRenameWithoutSchemaEvent() {
    var previous = stream();
    var updated = new SpDataStream(previous);
    updated.setName("Renamed stream");
    updated.setDescription("New description");
    var resources = mock(SpResourceManager.class);
    var streams = mock(DataStreamResourceManager.class);
    var publisher = mock(ApplicationEventPublisher.class);
    when(resources.manageDataStreams()).thenReturn(streams);
    when(streams.find("stream-1")).thenReturn(previous);

    try (var coordinators = mockConstruction(PipelineUpdateCoordinator.class)) {
      new DataStreamUpdateManagement(null, resources, publisher).updateDataStream(updated);

      verify(streams).update(updated);
      verify(coordinators.constructed().getFirst()).updatePipelines(updated);
      verifyNoInteractions(publisher);
    }
  }

  @Test
  void doesNotPublishWhenPersistenceFails() {
    var updated = stream();
    updated.getEventSchema().addEventProperty(new EventPropertyPrimitive("xs:string", "new-field", null, null));
    var resources = mock(SpResourceManager.class);
    var streams = mock(DataStreamResourceManager.class);
    var publisher = mock(ApplicationEventPublisher.class);
    when(resources.manageDataStreams()).thenReturn(streams);
    when(streams.find("stream-1")).thenReturn(stream());
    doThrow(new IllegalStateException("Persistence failed")).when(streams).update(updated);

    try (var coordinators = mockConstruction(PipelineUpdateCoordinator.class)) {
      var management = new DataStreamUpdateManagement(null, resources, publisher);
      assertThrows(IllegalStateException.class, () -> management.updateDataStream(updated));

      verifyNoInteractions(publisher, coordinators.constructed().getFirst());
    }
  }

  @Test
  void requiresEventPublisher() {
    assertThrows(NullPointerException.class,
        () -> new DataStreamUpdateManagement(null, mock(SpResourceManager.class), null));
  }

  private SpDataStream stream() {
    var stream = new SpDataStream();
    stream.setElementId("stream-1");
    stream.setName("Stream");
    return stream;
  }
}
