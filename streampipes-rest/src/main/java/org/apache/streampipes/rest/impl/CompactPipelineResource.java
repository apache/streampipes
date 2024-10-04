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

import org.apache.streampipes.manager.pipeline.PipelineManager;
import org.apache.streampipes.manager.pipeline.compact.CompactPipelineManagement;
import org.apache.streampipes.model.message.Notification;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.model.pipeline.compact.CompactPipeline;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/compact-pipelines")
public class CompactPipelineResource extends AbstractAuthGuardedRestResource {

  private final CompactPipelineManagement compactPipelineManagement;

  public CompactPipelineResource() {
    this.compactPipelineManagement = new CompactPipelineManagement(
        getPipelineElementStorage()
    );
  }

  @PostMapping(
      consumes = {
          MediaType.APPLICATION_JSON_VALUE,
          "application/yaml",
          "application/yml"
      }
  )
  @PreAuthorize(AuthConstants.HAS_WRITE_PIPELINE_PRIVILEGE)
  public ResponseEntity<?> addPipelineCompact(
      @RequestBody CompactPipeline compactPipeline
  ) throws Exception {

    var pipelineGenerationResult = compactPipelineManagement.makePipeline(compactPipeline);
    if (pipelineGenerationResult.allPipelineElementsValid()) {
      String pipelineId = PipelineManager.addPipeline(getAuthenticatedUserSid(), pipelineGenerationResult.pipeline());
      if (compactPipeline.createOptions().start()) {
        try {
          PipelineOperationStatus status = PipelineManager.startPipeline(pipelineId);
          return ok(status);
        } catch (Exception e) {
          return statusMessage(Notifications.error(NotificationType.UNKNOWN_ERROR));
        }
      }
      var message = Notifications.success("Pipeline stored");
      message.addNotification(new Notification("id", pipelineId));
      return ok(message);
    } else {
      return ResponseEntity.status(400).body(pipelineGenerationResult.validationInfos());
    }
  }

  @PutMapping(
      path = "{id}",
      consumes = {
          MediaType.APPLICATION_JSON_VALUE,
          "application/yaml",
          "application/yml"
      }
  )
  @PreAuthorize(AuthConstants.HAS_WRITE_PIPELINE_PRIVILEGE)
  public ResponseEntity<?> updatePipelineCompact(
      @PathVariable("id") String elementId,
      @RequestBody CompactPipeline compactPipeline
  ) throws Exception {

    return null;
  }
}
