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

import org.apache.streampipes.manager.template.PipelineTemplateGenerator;
import org.apache.streampipes.manager.template.PipelineTemplateInvocationGenerator;
import org.apache.streampipes.manager.template.PipelineTemplateInvocationHandler;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.SpDataStreamContainer;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.model.template.PipelineTemplateDescription;
import org.apache.streampipes.model.template.PipelineTemplateInvocation;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.shared.exception.SpMessageException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/pipeline-templates")
public class PipelineTemplate extends AbstractAuthGuardedRestResource {

  @GetMapping(path = "/streams", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SpDataStreamContainer> getAvailableDataStreams() {
    List<SpDataStream> sources = getPipelineElementRdfStorage().getAllDataStreams();
    List<SpDataStream> datasets = new ArrayList<>();

    sources.stream()
        .map(SpDataStream::new)
        .forEach(datasets::add);

    return ok((new SpDataStreamContainer(datasets)));
  }

  @GetMapping(
      path = "/invocation",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PipelineTemplateInvocation> getPipelineTemplateInvocation(
      @RequestParam(value = "streamId", required = false) String streamId,
      @RequestParam(value = "templateId") String pipelineTemplateId) {
    SpDataStream dataStream = getDataStream(streamId);
    var pipelineTemplateDescriptionOpt = getPipelineTemplateDescription(pipelineTemplateId);
    if (pipelineTemplateDescriptionOpt.isPresent()) {
      PipelineTemplateInvocation invocation =
          new PipelineTemplateInvocationGenerator(
              dataStream,
              pipelineTemplateDescriptionOpt.get()
          ).generateInvocation();
      PipelineTemplateInvocation clonedInvocation = new PipelineTemplateInvocation(invocation);
      return ok(new PipelineTemplateInvocation(clonedInvocation));
    } else {
      throw new SpMessageException(HttpStatus.BAD_REQUEST, Notifications.error(
          String.format(
              "Could not create pipeline template %s - did you install all pipeline elements?",
              pipelineTemplateId.substring(pipelineTemplateId.lastIndexOf(".") + 1))
      ));
    }
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PipelineOperationStatus> generatePipeline(
      @RequestBody PipelineTemplateInvocation pipelineTemplateInvocation) {

    PipelineOperationStatus status = new PipelineTemplateInvocationHandler(
        getAuthenticatedUserSid(),
        pipelineTemplateInvocation
    ).handlePipelineInvocation();
    return ok(status);
  }

  private Optional<PipelineTemplateDescription> getPipelineTemplateDescription(String pipelineTemplateId) {
    return new PipelineTemplateGenerator()
        .getAllPipelineTemplates()
        .stream()
        .filter(pt -> pt.getAppId().equals(pipelineTemplateId))
        .findFirst();
  }

  private List<SpDataStream> getAllDataStreams() {
    return getPipelineElementRdfStorage().getAllDataStreams();
  }

  private SpDataStream getDataStream(String streamId) {
    return getAllDataStreams()
        .stream()
        .filter(sp -> sp.getElementId().equals(streamId))
        .findFirst()
        .get();
  }
}
