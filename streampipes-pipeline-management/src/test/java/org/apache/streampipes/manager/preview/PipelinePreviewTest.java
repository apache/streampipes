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
package org.apache.streampipes.manager.preview;

import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.resource.management.SpResourceManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class PipelinePreviewTest {

  private static final String PREVIEW_ID = "preview-under-test";

  private final ExtensionServiceRequestManager requestManager = mock(ExtensionServiceRequestManager.class);
  private final PipelinePreview preview = new PipelinePreview(mock(SpResourceManager.class));

  @AfterEach
  void cleanUp() {
    ActivePipelinePreviews.INSTANCE.removePreview(PREVIEW_ID);
  }

  @Test
  void deletePreviewIsIdempotent() {
    ActivePipelinePreviews.INSTANCE.addActivePreview(PREVIEW_ID, List.of(new SpDataStream()));
    assertTrue(preview.isActive(PREVIEW_ID));

    preview.deletePreview(PREVIEW_ID, requestManager);
    assertDoesNotThrow(() -> preview.deletePreview(PREVIEW_ID, requestManager));

    assertFalse(preview.isActive(PREVIEW_ID));
    verifyNoInteractions(requestManager);
  }

  @Test
  void deletingAnUnknownPreviewDoesNothing() {
    assertDoesNotThrow(() -> preview.deletePreview("unknown-preview", requestManager));
    verifyNoInteractions(requestManager);
  }

  @Test
  void requestingStreamsOfAnUnknownPreviewIsRejected() {
    assertThrows(IllegalArgumentException.class,
        () -> preview.getPipelineElementPreviewStreams("unknown-preview"));
  }
}
