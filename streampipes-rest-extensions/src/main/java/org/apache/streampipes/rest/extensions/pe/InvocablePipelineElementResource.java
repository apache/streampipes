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
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.sdk.extractor.AbstractParameterExtractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;

public abstract class InvocablePipelineElementResource<
    K extends InvocableStreamPipesEntity,
    T extends IStreamPipesPipelineElement<PcT>,
    PcT extends IPipelineElementConfiguration<?, T>,
    V extends IStreamPipesRuntime<T, K>,
    W extends AbstractParameterExtractor<K>> extends AbstractPipelineElementResource<T> {

  private static final Logger LOG = LoggerFactory.getLogger(InvocablePipelineElementResource.class);
  protected Class<K> clazz;

  public InvocablePipelineElementResource(Class<K> clazz) {
    this.clazz = clazz;
  }

  protected abstract Map<String, T> getElementDeclarers();

  protected abstract String getInstanceId(String uri, String elementId);

  @POST
  @Path("{elementId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public jakarta.ws.rs.core.Response invokeRuntime(@PathParam("elementId") String elementId,
                                                 K graph) {

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
          LOG.error("Could not invoke pipeline element {} due to the following error: {}",
              graph.getName(),
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

  @POST
  @Path("{elementId}/configurations")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public jakarta.ws.rs.core.Response fetchConfigurations(@PathParam("elementId") String elementId,
                                                       RuntimeOptionsRequest req) {

    T declarer = getDeclarerById(elementId);
    RuntimeOptionsResponse responseOptions;

    try {
      if (declarer instanceof ResolvesContainerProvidedOptions) {
        responseOptions =
            new RuntimeResolvableRequestHandler().handleRuntimeResponse((ResolvesContainerProvidedOptions) declarer,
                req);
        return ok(responseOptions);
      } else if (declarer instanceof SupportsRuntimeConfig) {
        responseOptions =
            new RuntimeResolvableRequestHandler().handleRuntimeResponse((SupportsRuntimeConfig) declarer, req);
        return ok(responseOptions);
      } else {
        return jakarta.ws.rs.core.Response.status(500).build();
      }
    } catch (SpConfigurationException e) {
      return jakarta.ws.rs.core.Response
          .status(400)
          .entity(e)
          .build();
    }
  }

  @POST
  @Path("{elementId}/output")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public jakarta.ws.rs.core.Response fetchOutputStrategy(@PathParam("elementId") String elementId,
                                                       K runtimeOptionsRequest) {
    try {
      //I runtimeOptionsRequest = JacksonSerializer.getObjectMapper().readValue(payload, clazz);
      ResolvesContainerProvidedOutputStrategy<K, W> resolvesOutput =
          (ResolvesContainerProvidedOutputStrategy<K, W>)
              getDeclarerById
                  (elementId);
      return ok(resolvesOutput.resolveOutputStrategy
          (runtimeOptionsRequest, getExtractor(runtimeOptionsRequest)));
    } catch (SpRuntimeException | SpConfigurationException e) {
      return ok(new Response(elementId, false));
    }
  }


  // TODO move endpoint to /elementId/instances/runningInstanceId
  @DELETE
  @Path("{elementId}/{runningInstanceId}")
  @Produces(MediaType.APPLICATION_JSON)
  public jakarta.ws.rs.core.Response detach(@PathParam("elementId") String elementId,
                                          @PathParam("runningInstanceId") String runningInstanceId) {

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

  @GET
  @Path("{elementId}/instances")
  @Produces(MediaType.APPLICATION_JSON)
  public jakarta.ws.rs.core.Response listRunningInstances(@PathParam("elementId") String elementId) {
    return ok(RunningInstances.INSTANCE.getRunningInstanceIdsForElement(elementId));
  }

  protected abstract W getExtractor(K graph);

  protected abstract K createGroundingDebugInformation(K graph);

  protected abstract V getRuntime();

  protected abstract Response invokeRuntime(String instanceId,
                                            T pipelineElement,
                                            K graph);

  private Boolean isDebug() {
    return Environments.getEnvironment().getSpDebug().getValueOrDefault();
  }

  private String getServiceGroup() {
    return DeclarersSingleton.getInstance().getServiceDefinition().getServiceGroup();
  }
}

