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

package org.apache.streampipes.rest.impl;

import org.apache.streampipes.model.canvas.PipelineCanvasMetadata;
import org.apache.streampipes.storage.api.pipeline.IPipelineCanvasMetadataStorage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PipelineCanvasMetadataResourceTest {

  private final IPipelineCanvasMetadataStorage storage = mock(IPipelineCanvasMetadataStorage.class);
  private final PipelineCanvasMetadataResource resource = new PipelineCanvasMetadataResource(storage);

  @Test
  void updatesExistingMetadataWithStoredIdentityAndRevision() {
    var existing = new PipelineCanvasMetadata();
    existing.setId("stored");
    existing.setRev("revision");
    when(storage.getPipelineCanvasMetadataForPipeline("pipeline")).thenReturn(existing);
    var incoming = new PipelineCanvasMetadata();

    resource.updatePipelineCanvasMetadata("pipeline", incoming);

    assertEquals("pipeline", incoming.getPipelineId());
    assertEquals("stored", incoming.getId());
    assertEquals("revision", incoming.getRev());
    verify(storage).updateElement(incoming);
    verify(storage, never()).persist(any());
  }

  @Test
  void createsMetadataWithoutClientSuppliedIdentity() {
    var incoming = new PipelineCanvasMetadata();
    incoming.setId("client-id");
    incoming.setRev("client-revision");

    resource.updatePipelineCanvasMetadata("pipeline", incoming);

    assertEquals("pipeline", incoming.getPipelineId());
    assertNull(incoming.getId());
    assertNull(incoming.getRev());
    verify(storage).persist(incoming);
    verify(storage, never()).updateElement(any());
  }

  @Test
  void deletingMissingMetadataIsIdempotent() {
    resource.deletePipelineCanvasMetadataForPipeline("missing");

    verify(storage, never()).deleteElement(any());
  }
}
