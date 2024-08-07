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
import org.apache.streampipes.manager.runtime.DataStreamRuntimeInfoProvider;
import org.apache.streampipes.manager.runtime.RateLimitedRuntimeInfoProvider;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.preview.PipelinePreviewModel;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.shared.exception.BadRequestException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import jakarta.servlet.http.HttpServletResponse;

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

  @GetMapping(path = "{previewId}",
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public StreamingResponseBody getPipelinePreviewResult(
      HttpServletResponse response,
      @PathVariable("previewId") String previewId) {
    try {
      // deactivate nginx proxy buffering for better performance of streaming output
      response.addHeader("X-Accel-Buffering", "no");
      var spDataStreams = new PipelinePreview().getPipelineElementPreviewStreams(previewId);
      var runtimeInfoFetcher = new DataStreamRuntimeInfoProvider(spDataStreams);
      var runtimeInfoProvider = new RateLimitedRuntimeInfoProvider(runtimeInfoFetcher);
      return runtimeInfoProvider::streamOutput;
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("Could not generate preview", e);
    }
  }

  @DeleteMapping(path = "{previewId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> deletePipelinePreviewRequest(@PathVariable("previewId") String previewId) {
    new PipelinePreview().deletePreview(previewId);
    return ok();
  }

}
