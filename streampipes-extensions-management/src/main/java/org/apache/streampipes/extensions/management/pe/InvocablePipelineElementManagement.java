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

package org.apache.streampipes.extensions.management.pe;

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
import org.apache.streampipes.extensions.management.init.RunningInstances;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.model.runtime.RuntimeOptionsResponse;
import org.apache.streampipes.sdk.extractor.AbstractParameterExtractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public abstract class InvocablePipelineElementManagement<
    K extends InvocableStreamPipesEntity,
    T extends IStreamPipesPipelineElement<PcT>,
    PcT extends IPipelineElementConfiguration<?, T>,
    V extends IStreamPipesRuntime<T, K>,
    W extends AbstractParameterExtractor<K>> {

  private static final Logger LOG = LoggerFactory.getLogger(InvocablePipelineElementManagement.class);

  private final RuntimeResolvableRequestHandler runtimeResolvableRequestHandler;
  private final RunningInstances runningInstances;

  public InvocablePipelineElementManagement() {
    this(new RuntimeResolvableRequestHandler(), RunningInstances.INSTANCE);
  }

  public InvocablePipelineElementManagement(RuntimeResolvableRequestHandler runtimeResolvableRequestHandler,
                                            RunningInstances runningInstances) {
    this.runtimeResolvableRequestHandler = runtimeResolvableRequestHandler;
    this.runningInstances = runningInstances;
  }

  public Response invokeRuntime(String appId, K graph) {
    if (isDebug()) {
      LOG.info("SP_DEBUG env variable is set - overriding broker hostname and port for local development");
      graph = createGroundingDebugInformation(graph);
    }

    T declarer = getDeclarerById(appId).declareConfig().getSupplier().get();

    if (declarer != null) {
      String runningInstanceId = getInstanceId(graph.getElementId(), appId);
      if (!runningInstances.exists(runningInstanceId)) {
        Response response = invokeRuntime(runningInstanceId, declarer, graph);
        if (!response.isSuccess()) {
          LOG.error("Could not invoke pipeline element {} due to the following error: {}",
              graph.getName(),
              response.getOptionalMessage());
          runningInstances.remove(runningInstanceId);
        }
        return response;
      } else {
        LOG.info("Pipeline element {} with id {} seems to be already running, skipping invocation request.",
            graph.getName(), runningInstanceId);
        return new Response(graph.getElementId(), true);
      }

    }

    return new Response(appId, false, "Could not find the element with id: " + appId);
  }

  public RuntimeOptionsResponse fetchConfigurations(String elementId,
                                                    RuntimeOptionsRequest runtimeOptionsRequest)
      throws SpConfigurationException {
    T declarer = getDeclarerById(elementId);
    if (declarer instanceof ResolvesContainerProvidedOptions) {
      return runtimeResolvableRequestHandler.handleRuntimeResponse((ResolvesContainerProvidedOptions) declarer,
          runtimeOptionsRequest);
    } else if (declarer instanceof SupportsRuntimeConfig) {
      return runtimeResolvableRequestHandler.handleRuntimeResponse((SupportsRuntimeConfig) declarer,
          runtimeOptionsRequest);
    } else {
      throw new SpRuntimeException(
          "This element does not support dynamic options - is the pipeline element description up to date?");
    }
  }

  @SuppressWarnings("unchecked")
  public Object fetchOutputStrategy(String elementId,
                                    K runtimeOptionsRequest) throws SpConfigurationException {
    ResolvesContainerProvidedOutputStrategy<K, W> resolvesOutput =
        (ResolvesContainerProvidedOutputStrategy<K, W>) getDeclarerById(elementId);
    return resolvesOutput.resolveOutputStrategy(runtimeOptionsRequest, getExtractor(runtimeOptionsRequest));
  }

  public Response detach(String elementId, String runningInstanceId) {
    IStreamPipesRuntime<?, ?> runningInstance = runningInstances.getInvocation(runningInstanceId);

    if (runningInstance != null) {
      Response response = runningInstance.onRuntimeDetached(runningInstanceId);

      if (response.isSuccess()) {
        runningInstances.remove(runningInstanceId);
      }

      return response;
    }

    return new Response(elementId, false, "Could not find the running instance with id: " + runningInstanceId);
  }

  public List<String> listRunningInstances(String elementId) {
    return runningInstances.getRunningInstanceIdsForElement(elementId);
  }

  public abstract Map<String, T> getElementDeclarers();

  protected RunningInstances getRunningInstances() {
    return runningInstances;
  }

  protected T getDeclarerById(String appId) {
    return getElementDeclarers().get(appId);
  }

  protected abstract String getInstanceId(String uri, String elementId);

  protected abstract W getExtractor(K graph);

  protected abstract K createGroundingDebugInformation(K graph);

  protected abstract V getRuntime();

  protected abstract Response invokeRuntime(String instanceId,
                                            T pipelineElement,
                                            K graph);

  private boolean isDebug() {
    return Environments.getEnvironment().getSpDebug().getValueOrDefault();
  }
}

