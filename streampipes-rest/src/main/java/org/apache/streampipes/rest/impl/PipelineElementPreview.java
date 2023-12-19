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

import org.apache.streampipes.manager.preview.PipelinePreview;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.preview.PipelinePreviewModel;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/pipeline-element-preview")
public class PipelineElementPreview extends AbstractAuthGuardedRestResource {


  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<PipelinePreviewModel> requestPipelinePreview(@RequestBody Pipeline pipeline) {
    PipelinePreviewModel previewModel = new PipelinePreview().initiatePreview(pipeline);

    return ok(previewModel);
  }

  @GetMapping(path = "{previewId}/{pipelineElementDomId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getPipelinePreviewResult(@PathVariable("previewId") String previewId,
                                           @PathVariable("pipelineElementDomId") String pipelineElementDomId) {
    try {
      String runtimeInfo = new PipelinePreview().getPipelineElementPreview(previewId, pipelineElementDomId);
      return ok(runtimeInfo);
    } catch (IllegalArgumentException e) {
      return badRequest();
    }
  }

  @DeleteMapping(path = "{previewId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> deletePipelinePreviewRequest(@PathVariable("previewId") String previewId) {
    new PipelinePreview().deletePreview(previewId);
    return ok();
  }

}
