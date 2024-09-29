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

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;
import org.apache.streampipes.extensions.api.pe.config.IPipelineElementConfiguration;
import org.apache.streampipes.extensions.api.pe.runtime.IStreamPipesRuntime;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOptions;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOutputStrategy;
import org.apache.streampipes.extensions.api.runtime.SupportsRuntimeConfig;
import org.apache.streampipes.extensions.management.api.RuntimeResolvableRequestHandler;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.init.RunningInstances;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.model.runtime.RuntimeOptionsResponse;
import org.apache.streampipes.rest.extensions.AbstractPipelineElementResource;
import org.apache.streampipes.sdk.extractor.AbstractParameterExtractor;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class InvocablePipelineElementResource<K extends InvocableStreamPipesEntity, T extends IStreamPipesPipelineElement<PcT>, PcT extends IPipelineElementConfiguration<?, T>, V extends IStreamPipesRuntime<T, K>, W extends AbstractParameterExtractor<K>>
        extends
          AbstractPipelineElementResource<T> {

  private static final Logger LOG = LoggerFactory.getLogger(InvocablePipelineElementResource.class);
  protected Class<K> clazz;

  public InvocablePipelineElementResource(Class<K> clazz) {
    this.clazz = clazz;
  }

  protected abstract Map<String, T> getElementDeclarers();

  protected abstract String getInstanceId(String uri, String elementId);

  @PostMapping(path = "{elementId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response> invokeRuntime(@PathVariable("elementId") String elementId, @RequestBody K graph) {

    if (isDebug()) {
      LOG.info("SP_DEBUG env variable is set - overriding broker hostname and port for local development");
      graph = createGroundingDebugInformation(graph);
    }

    T declarer = getDeclarerById(elementId).declareConfig().getSupplier().get();

    if (declarer != null) {
      String runningInstanceId = getInstanceId(graph.getElementId(), elementId);
      if (!RunningInstances.INSTANCE.exists(runningInstanceId)) {
        Response resp = invokeRuntime(runningInstanceId, declarer, graph);
        if (!resp.isSuccess()) {
          LOG.error("Could not invoke pipeline element {} due to the following error: {}", graph.getName(),
                  resp.getOptionalMessage());
          RunningInstances.INSTANCE.remove(runningInstanceId);
        }
        return ok(resp);
      } else {
        LOG.info("Pipeline element {} with id {} seems to be already running, skipping invocation request.",
                graph.getName(), runningInstanceId);
        Response resp = new Response(graph.getElementId(), true);
        return ok(resp);
      }

    }

    return ok(new Response(elementId, false, "Could not find the element with id: " + elementId));
  }

  @PostMapping(path = "{elementId}/configurations", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> fetchConfigurations(@PathVariable("elementId") String elementId,
          @RequestBody RuntimeOptionsRequest req) {

    T declarer = getDeclarerById(elementId);
    RuntimeOptionsResponse responseOptions;

    try {
      if (declarer instanceof ResolvesContainerProvidedOptions) {
        responseOptions = new RuntimeResolvableRequestHandler()
                .handleRuntimeResponse((ResolvesContainerProvidedOptions) declarer, req);
        return ok(responseOptions);
      } else if (declarer instanceof SupportsRuntimeConfig) {
        responseOptions = new RuntimeResolvableRequestHandler().handleRuntimeResponse((SupportsRuntimeConfig) declarer,
                req);
        return ok(responseOptions);
      } else {
        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
      }
    } catch (SpConfigurationException e) {
      return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(e);
    }
  }

  @PostMapping(path = "{elementId}/output", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> fetchOutputStrategy(@PathVariable("elementId") String elementId,
          @RequestBody K runtimeOptionsRequest) {
    try {
      // I runtimeOptionsRequest = JacksonSerializer.getObjectMapper().readValue(payload, clazz);
      ResolvesContainerProvidedOutputStrategy<K, W> resolvesOutput = (ResolvesContainerProvidedOutputStrategy<K, W>) getDeclarerById(
              elementId);
      return ok(resolvesOutput.resolveOutputStrategy(runtimeOptionsRequest, getExtractor(runtimeOptionsRequest)));
    } catch (SpRuntimeException | SpConfigurationException e) {
      return ok(new Response(elementId, false));
    }
  }

  // TODO move endpoint to /elementId/instances/runningInstanceId
  @DeleteMapping(path = "{elementId}/{runningInstanceId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response> detach(@PathVariable("elementId") String elementId,
          @PathVariable("runningInstanceId") String runningInstanceId) {

    IStreamPipesRuntime<?, ?> runningInstance = RunningInstances.INSTANCE.getInvocation(runningInstanceId);

    if (runningInstance != null) {
      Response resp = runningInstance.onRuntimeDetached(runningInstanceId);

      if (resp.isSuccess()) {
        RunningInstances.INSTANCE.remove(runningInstanceId);
      }

      return ok(resp);
    }

    return ok(new Response(elementId, false, "Could not find the running instance with id: " + runningInstanceId));
  }

  @GetMapping(path = "{elementId}/instances", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> listRunningInstances(@PathVariable("elementId") String elementId) {
    return ok(RunningInstances.INSTANCE.getRunningInstanceIdsForElement(elementId));
  }

  protected abstract W getExtractor(K graph);

  protected abstract K createGroundingDebugInformation(K graph);

  protected abstract V getRuntime();

  protected abstract Response invokeRuntime(String instanceId, T pipelineElement, K graph);

  private Boolean isDebug() {
    return Environments.getEnvironment().getSpDebug().getValueOrDefault();
  }

  private String getServiceGroup() {
    return DeclarersSingleton.getInstance().getServiceDefinition().getServiceGroup();
  }
}
