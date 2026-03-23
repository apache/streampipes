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

package org.apache.streampipes.rest.extensions.pe;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;
import org.apache.streampipes.extensions.api.pe.config.IPipelineElementConfiguration;
import org.apache.streampipes.extensions.api.pe.runtime.IStreamPipesRuntime;
import org.apache.streampipes.extensions.management.pe.InvocablePipelineElementManagement;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.rest.extensions.AbstractPipelineElementResource;
import org.apache.streampipes.sdk.extractor.AbstractParameterExtractor;

import org.apache.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public abstract class InvocablePipelineElementResource<
    K extends InvocableStreamPipesEntity,
    T extends IStreamPipesPipelineElement<PcT>,
    PcT extends IPipelineElementConfiguration<?, T>,
    V extends IStreamPipesRuntime<T, K>,
    W extends AbstractParameterExtractor<K>> extends AbstractPipelineElementResource<T> {

  private final InvocablePipelineElementManagement<K, T, PcT, V, W> pipelineElementManagement;

  public InvocablePipelineElementResource(
      InvocablePipelineElementManagement<K, T, PcT, V, W> pipelineElementManagement) {
    super(pipelineElementManagement);
    this.pipelineElementManagement = pipelineElementManagement;
  }

  @PostMapping(
      path = "{appId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response> invokeRuntime(@PathVariable("appId") String appId,
                                         @RequestBody K graph) {
    return ok(pipelineElementManagement.invokeRuntime(appId, graph));
  }

  @PostMapping(
      path = "{elementId}/configurations",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> fetchConfigurations(@PathVariable("elementId") String elementId,
                                               @RequestBody RuntimeOptionsRequest req) {
    try {
      return ok(pipelineElementManagement.fetchConfigurations(elementId, req));
    } catch (SpConfigurationException e) {
      return ResponseEntity
          .status(HttpStatus.SC_BAD_REQUEST)
          .body(e);
    } catch (SpRuntimeException e) {
      return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping(
      path = "{elementId}/output",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> fetchOutputStrategy(@PathVariable("elementId") String elementId,
                                               @RequestBody K runtimeOptionsRequest) {
    try {
      return ok(pipelineElementManagement.fetchOutputStrategy(elementId, runtimeOptionsRequest));
    } catch (SpRuntimeException | SpConfigurationException e) {
      return ok(new Response(elementId, false));
    }
  }


  // TODO move endpoint to /elementId/instances/runningInstanceId
  @DeleteMapping(path = "{elementId}/{runningInstanceId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response> detach(@PathVariable("elementId") String elementId,
                                         @PathVariable("runningInstanceId") String runningInstanceId) {
    return ok(pipelineElementManagement.detach(elementId, runningInstanceId));
  }

  @GetMapping(path = "{elementId}/instances", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> listRunningInstances(@PathVariable("elementId") String elementId) {
    return ok(pipelineElementManagement.listRunningInstances(elementId));
  }

}
