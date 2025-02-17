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
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.storage.api.IPipelineCanvasMetadataStorage;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/pipeline-canvas-metadata")
public class PipelineCanvasMetadataResource extends AbstractRestResource {

  @GetMapping(path = "/pipeline/{pipelineId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PipelineCanvasMetadata> getPipelineCanvasMetadataForPipeline(
      @PathVariable("pipelineId") String pipelineId) {
    try {
      return ok(getPipelineCanvasMetadataStorage()
          .getPipelineCanvasMetadataForPipeline(pipelineId));
    } catch (IllegalArgumentException e) {
      throw new SpMessageException(HttpStatus.BAD_REQUEST, Notifications.error(e.getMessage()));
    }
  }

  @GetMapping(path = "{canvasId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PipelineCanvasMetadata> getPipelineCanvasMetadata(
      @PathVariable("canvasId") String pipelineCanvasId) {
    try {
      return ok(getPipelineCanvasMetadataStorage()
          .getElementById(pipelineCanvasId));
    } catch (IllegalArgumentException e) {
      throw new SpMessageException(HttpStatus.BAD_REQUEST, Notifications.error(e.getMessage()));
    }
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> storePipelineCanvasMetadata(@RequestBody PipelineCanvasMetadata pipelineCanvasMetadata) {
    getPipelineCanvasMetadataStorage().persist(pipelineCanvasMetadata);
    return ok();
  }

  @DeleteMapping(
      path = "{canvasId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> deletePipelineCanvasMetadata(@PathVariable("canvasId") String pipelineCanvasId) {
    PipelineCanvasMetadata metadata = find(pipelineCanvasId);
    getPipelineCanvasMetadataStorage().deleteElement(metadata);
    return ok();
  }

  @DeleteMapping(
      path = "/pipeline/{pipelineId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> deletePipelineCanvasMetadataForPipeline(@PathVariable("pipelineId") String pipelineId) {
    PipelineCanvasMetadata metadata =
        getPipelineCanvasMetadataStorage().getPipelineCanvasMetadataForPipeline(pipelineId);
    getPipelineCanvasMetadataStorage().deleteElement(metadata);
    return ok();
  }

  @PutMapping(
      path = "{canvasId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updatePipelineCanvasMetadata(@PathVariable("canvasId") String pipelineCanvasId,
                                                           @RequestBody PipelineCanvasMetadata pipelineCanvasMetadata) {
    try {
      var existing = getPipelineCanvasMetadataStorage().getElementById(pipelineCanvasMetadata.getId());
      pipelineCanvasMetadata.setRev(existing.getRev());
      getPipelineCanvasMetadataStorage().updateElement(pipelineCanvasMetadata);
    } catch (IllegalArgumentException e) {
      getPipelineCanvasMetadataStorage().persist(pipelineCanvasMetadata);
    }
    return ok();
  }

  private PipelineCanvasMetadata find(String canvasId) {
    return getPipelineCanvasMetadataStorage().getElementById(canvasId);
  }

  private IPipelineCanvasMetadataStorage getPipelineCanvasMetadataStorage() {
    return getNoSqlStorage().getPipelineCanvasMetadataStorage();
  }
}
