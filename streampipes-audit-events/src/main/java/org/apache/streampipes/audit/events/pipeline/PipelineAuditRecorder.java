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

package org.apache.streampipes.audit.events.pipeline;

import org.apache.streampipes.audit.api.AuditOutcome;
import org.apache.streampipes.audit.api.AuditService;
import org.apache.streampipes.audit.events.StandardAuditEvents;

import java.util.Objects;

/** Pipeline audit publication and safe payload construction; outcomes are decided by the operation. */
public final class PipelineAuditRecorder {
  private final AuditService auditService;

  public PipelineAuditRecorder(AuditService auditService) {
    this.auditService = Objects.requireNonNull(auditService);
  }

  public void created(String actor, String pipelineId, AuditOutcome outcome) {
    auditService.record(StandardAuditEvents.PIPELINE_CREATE, outcome, actor, pipelineId,
        new PipelineCreatedDetails());
  }

  public void started(String actor, String pipelineId, AuditOutcome outcome) {
    auditService.record(StandardAuditEvents.PIPELINE_START, outcome, actor, pipelineId, null);
  }

  public void stopped(String actor, String pipelineId, AuditOutcome outcome, boolean forced) {
    auditService.record(StandardAuditEvents.PIPELINE_STOP, outcome, actor, pipelineId,
        new PipelineLifecycleDetails(forced));
  }

  public void edited(String actor, String pipelineId, AuditOutcome outcome, PipelineEditedDetails details) {
    if (details == null || !details.changes().isEmpty()) {
      auditService.record(StandardAuditEvents.PIPELINE_EDIT, outcome, actor, pipelineId, details);
    }
  }
}
