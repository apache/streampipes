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
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.resource.management.SpResourceManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class PipelinePreviewTest {

  private static final String PREVIEW_ID = "preview-under-test";

  private final ExtensionServiceRequestManager requestManager = mock(ExtensionServiceRequestManager.class);
  private final PipelinePreview preview = new PipelinePreview(mock(SpResourceManager.class));
  private final PipelinePreview shortGracePreview =
      new PipelinePreview(mock(SpResourceManager.class), Duration.ofMillis(50));

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

  @Test
  void expiredStreamReleasesThePreviewWhenNoClientReattaches() throws InterruptedException {
    ActivePipelinePreviews.INSTANCE.addActivePreview(PREVIEW_ID, List.of(new SpDataStream()));

    shortGracePreview.onStreamExpired(PREVIEW_ID, requestManager);

    assertTrue(awaitInactive(Duration.ofSeconds(2)), "preview should be released after the grace period");
  }

  @Test
  void reattachingWithinTheGracePeriodKeepsThePreviewRunning() throws InterruptedException {
    ActivePipelinePreviews.INSTANCE.addActivePreview(PREVIEW_ID, List.of(new SpDataStream()));

    shortGracePreview.onStreamExpired(PREVIEW_ID, requestManager);
    shortGracePreview.onStreamAttached(PREVIEW_ID);

    Thread.sleep(200);
    assertTrue(preview.isActive(PREVIEW_ID));
  }

  @Test
  void expiryOfAnAlreadyStoppedPreviewSchedulesNothing() throws InterruptedException {
    shortGracePreview.onStreamExpired(PREVIEW_ID, requestManager);

    Thread.sleep(200);
    assertFalse(preview.isActive(PREVIEW_ID));
    verifyNoInteractions(requestManager);
  }

  private boolean awaitInactive(Duration timeout) throws InterruptedException {
    var deadline = System.nanoTime() + timeout.toNanos();
    while (System.nanoTime() < deadline) {
      if (!preview.isActive(PREVIEW_ID)) {
        return true;
      }
      Thread.sleep(10);
    }
    return false;
  }

  @Test
  void previewIsReleasedAtItsMaximumDurationEvenWithAnAttachedStream() throws InterruptedException {
    var cappedPreview = new PipelinePreview(
        mock(SpResourceManager.class), Duration.ofSeconds(30), Duration.ofMillis(100));
    ActivePipelinePreviews.INSTANCE.addActivePreview(PREVIEW_ID, List.of(new SpDataStream()));

    cappedPreview.limitLifetime(PREVIEW_ID, requestManager);
    cappedPreview.onStreamAttached(PREVIEW_ID);

    assertTrue(awaitInactive(Duration.ofSeconds(2)), "preview should be released at its maximum duration");
  }

  @Test
  void stoppingAPreviewCancelsItsLifetimeLimit() throws InterruptedException {
    var cappedPreview = new PipelinePreview(
        mock(SpResourceManager.class), Duration.ofSeconds(30), Duration.ofMillis(100));
    ActivePipelinePreviews.INSTANCE.addActivePreview(PREVIEW_ID, List.of(new SpDataStream()));
    cappedPreview.limitLifetime(PREVIEW_ID, requestManager);

    cappedPreview.deletePreview(PREVIEW_ID, requestManager);
    // a new preview under the same id must not be released by the old limit
    ActivePipelinePreviews.INSTANCE.addActivePreview(PREVIEW_ID, List.of(new SpDataStream()));

    Thread.sleep(300);
    assertTrue(preview.isActive(PREVIEW_ID));
  }

  @Test
  void elementsThatMayHaveStartedStayTrackedSoTheyAreDetached() {
    var stream = processorWithId("stream");
    var running = processorWithId("running");
    var timedOut = processorWithId("timed-out");
    var neverSent = processorWithId("never-sent");

    var tracked = PipelinePreview.elementsToTrack(
        List.of(stream, running, timedOut, neverSent),
        Map.of(
            "timed-out", new PipelinePreview.InvocationFailure("Read timed out", true),
            "never-sent", new PipelinePreview.InvocationFailure("No extensions service", false)
        )
    );

    assertEquals(List.of("stream", "running", "timed-out"),
        tracked.stream().map(NamedStreamPipesEntity::getElementId).toList());
  }

  private static DataProcessorInvocation processorWithId(String elementId) {
    var processor = new DataProcessorInvocation();
    processor.setElementId(elementId);
    return processor;
  }
}
