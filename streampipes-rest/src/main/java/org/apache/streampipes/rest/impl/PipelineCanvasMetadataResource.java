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
import org.apache.streampipes.model.client.user.DefaultPrivilege;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.storage.api.pipeline.IPipelineCanvasMetadataStorage;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/pipeline-canvas-metadata")
public class PipelineCanvasMetadataResource extends AbstractAuthGuardedRestResource {

  @GetMapping(path = "/pipeline/{pipelineId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasReadAuthority() and hasPermission(#pipelineId, 'READ')")
  public ResponseEntity<PipelineCanvasMetadata> getPipelineCanvasMetadataForPipeline(
      @PathVariable("pipelineId") String pipelineId) {
    try {
      return ok(getPipelineCanvasMetadataStorage()
          .getPipelineCanvasMetadataForPipeline(pipelineId));
    } catch (IllegalArgumentException e) {
      throw new SpMessageException(HttpStatus.BAD_REQUEST, Notifications.error(e.getMessage()));
    }
  }

  @DeleteMapping(
      path = "/pipeline/{pipelineId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasWriteAuthority() and hasPermission(#pipelineId, 'WRITE')")
  public ResponseEntity<Void> deletePipelineCanvasMetadataForPipeline(@PathVariable("pipelineId") String pipelineId) {
    PipelineCanvasMetadata metadata =
        getPipelineCanvasMetadataStorage().getPipelineCanvasMetadataForPipeline(pipelineId);
    if (metadata != null) {
      getPipelineCanvasMetadataStorage().deleteElement(metadata);
    }
    return ok();
  }

  @PutMapping(
      path = "/pipeline/{pipelineId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasWriteAuthority() and hasPermission(#pipelineId, 'WRITE')")
  public ResponseEntity<Void> updatePipelineCanvasMetadata(@PathVariable("pipelineId") String pipelineId,
                                                           @RequestBody PipelineCanvasMetadata pipelineCanvasMetadata) {
    var existing = getPipelineCanvasMetadataStorage().getPipelineCanvasMetadataForPipeline(pipelineId);
    pipelineCanvasMetadata.setPipelineId(pipelineId);
    if (existing != null) {
      pipelineCanvasMetadata.setId(existing.getId());
      pipelineCanvasMetadata.setRev(existing.getRev());
      getPipelineCanvasMetadataStorage().updateElement(pipelineCanvasMetadata);
    } else {
      pipelineCanvasMetadata.setId(null);
      pipelineCanvasMetadata.setRev(null);
      getPipelineCanvasMetadataStorage().persist(pipelineCanvasMetadata);
    }
    return ok();
  }

  private IPipelineCanvasMetadataStorage getPipelineCanvasMetadataStorage() {
    return getNoSqlStorage().getPipelineCanvasMetadataStorage();
  }

  public boolean hasWriteAuthority() {
    return isAdminOrHasAnyAuthority(DefaultPrivilege.Constants.PRIVILEGE_WRITE_PIPELINE_VALUE);
  }

  public boolean hasReadAuthority() {
    return isAdminOrHasAnyAuthority(DefaultPrivilege.Constants.PRIVILEGE_READ_PIPELINE_VALUE);
  }
}
