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

package org.apache.streampipes.manager.pipeline;

import org.apache.streampipes.audit.api.AuditOutcome;
import org.apache.streampipes.audit.api.AuditService;
import org.apache.streampipes.audit.events.StandardAuditEvents;
import org.apache.streampipes.audit.events.pipeline.PipelineLifecycleDetails;
import org.apache.streampipes.manager.execution.PipelineExecutor;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.resource.management.PipelineResourceManager;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PipelineLifecycleAuditTest {
  private final AuditService audit = mock(AuditService.class);
  private final IPipelineStorage storage = mock(IPipelineStorage.class);
  private final Pipeline pipeline = new Pipeline();

  @Test
  void successfulStartRecordsTheAuthenticatedActor() {
    var status = status(true);
    var manager = manager();

    try (var executors = mockConstruction(PipelineExecutor.class,
        (executor, context) -> when(executor.startPipeline()).thenReturn(status))) {
      assertSame(status, manager.startPipeline("pipeline-1", null, "user-1"));
    }

    verify(audit).record(StandardAuditEvents.PIPELINE_START, AuditOutcome.SUCCEEDED,
        "user-1", "pipeline-1", null);
  }

  @Test
  void failedStartRecordsTheFailureBeforeRethrowing() {
    var manager = manager();

    try (var executors = mockConstruction(PipelineExecutor.class,
        (executor, context) -> when(executor.startPipeline()).thenThrow(new IllegalStateException()))) {
      assertThrows(IllegalStateException.class, () -> manager.startPipeline("pipeline-1", null, "user-1"));
    }

    verify(audit).record(StandardAuditEvents.PIPELINE_START, AuditOutcome.FAILED,
        "user-1", "pipeline-1", null);
  }

  @Test
  void forcedStopRecordsItsRequestedMode() {
    var status = status(false);
    var manager = manager();

    try (var executors = mockConstruction(PipelineExecutor.class,
        (executor, context) -> when(executor.stopPipeline(true)).thenReturn(status))) {
      assertSame(status, manager.stopPipeline("pipeline-1", true, null, "user-1"));
    }

    verify(audit).record(StandardAuditEvents.PIPELINE_STOP, AuditOutcome.FAILED,
        "user-1", "pipeline-1", new PipelineLifecycleDetails(true));
  }

  private PipelineManager manager() {
    var resources = mock(SpResourceManager.class);
    var pipelines = mock(PipelineResourceManager.class);
    pipeline.setPipelineId("pipeline-1");
    when(resources.getAuditService()).thenReturn(audit);
    when(resources.managePipelines()).thenReturn(pipelines);
    when(pipelines.getDb()).thenReturn(storage);
    when(storage.getElementById("pipeline-1")).thenReturn(pipeline);
    return new PipelineManager(resources);
  }

  private PipelineOperationStatus status(boolean success) {
    var status = new PipelineOperationStatus();
    status.setSuccess(success);
    return status;
  }
}
