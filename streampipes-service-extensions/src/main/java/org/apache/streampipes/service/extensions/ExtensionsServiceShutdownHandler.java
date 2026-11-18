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

package org.apache.streampipes.service.extensions;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.commons.exceptions.SpHttpErrorStatusCode;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.pe.runtime.IStreamPipesRuntime;
import org.apache.streampipes.extensions.management.client.StreamPipesClientResolver;
import org.apache.streampipes.extensions.management.connect.AdapterTransitionRegistry;
import org.apache.streampipes.extensions.management.connect.AdapterWorkerManagement;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.init.RunningAdapterInstances;
import org.apache.streampipes.extensions.management.init.RunningInstances;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.function.FunctionShutdownResult;
import org.apache.streampipes.model.function.FunctionsShutdownResponse;
import org.apache.streampipes.service.extensions.function.StreamPipesFunctionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Supplier;

/**
 * Stops everything this extension service is running when the service shuts down.
 *
 * <p>By the time the shutdown handler runs, the service no longer accepts requests from
 * the core, so it cannot wait for detach requests. Instead, pipeline elements, adapters
 * and functions are stopped locally. Nothing is reported back to the core on purpose:
 * pipelines and adapters remain marked as running in storage, and the core's health
 * checks re-attach them as soon as this service is registered again. Function state is
 * the one thing that is persisted, since it would otherwise be lost. It is persisted first,
 * because the core only keeps serving requests for a short while after it received its own
 * shutdown signal, and stopping adapters and pipeline elements can take seconds. Persisting
 * requires the core's function state endpoints ({@code SP_FUNCTION_STATE_ENDPOINTS_ENABLED}).
 *
 * <p>Services that run instances outside the registries known here, register an
 * {@link ExtensionServiceShutdownHook} to stop them.
 */
public class ExtensionsServiceShutdownHandler {

  private static final Logger LOG = LoggerFactory.getLogger(ExtensionsServiceShutdownHandler.class);

  private final RunningInstances runningInstances;
  private final AdapterWorkerManagement adapterManagement;
  private final Supplier<FunctionsShutdownResponse> functionShutdown;
  private final Supplier<StreamPipesClient> clientSupplier;
  private final List<ExtensionServiceShutdownHook> additionalHooks;

  public ExtensionsServiceShutdownHandler() {
    this(List.of());
  }

  public ExtensionsServiceShutdownHandler(List<ExtensionServiceShutdownHook> additionalHooks) {
    this(
        RunningInstances.INSTANCE,
        new AdapterWorkerManagement(
            RunningAdapterInstances.INSTANCE,
            DeclarersSingleton.getInstance(),
            AdapterTransitionRegistry.INSTANCE
        ),
        StreamPipesFunctionHandler.INSTANCE::shutdownFunctionsAndGetState,
        () -> new StreamPipesClientResolver().makeStreamPipesClientInstance(),
        additionalHooks
    );
  }

  ExtensionsServiceShutdownHandler(RunningInstances runningInstances,
                                   AdapterWorkerManagement adapterManagement,
                                   Supplier<FunctionsShutdownResponse> functionShutdown,
                                   Supplier<StreamPipesClient> clientSupplier,
                                   List<ExtensionServiceShutdownHook> additionalHooks) {
    this.runningInstances = runningInstances;
    this.adapterManagement = adapterManagement;
    this.functionShutdown = functionShutdown;
    this.clientSupplier = clientSupplier;
    this.additionalHooks = List.copyOf(additionalHooks);
  }

  public void onShutdown() {
    LOG.info("Shutting down StreamPipes extensions service...");
    stopFunctions();
    stopPipelineElements();
    stopAdapters();
    runAdditionalHooks();
  }

  private void runAdditionalHooks() {
    additionalHooks.forEach(hook -> {
      try {
        hook.onShutdown();
      } catch (RuntimeException e) {
        LOG.warn("Shutdown hook {} threw an exception", hook.getClass().getName(), e);
      }
    });
  }

  private void stopPipelineElements() {
    var instanceKeys = runningInstances.getRunningInstanceKeys();
    LOG.info("Stopping {} running pipeline elements...", instanceKeys.size());
    instanceKeys.forEach(this::stopPipelineElement);
  }

  private void stopPipelineElement(String instanceKey) {
    IStreamPipesRuntime<?, ?> runtime = runningInstances.getInvocation(instanceKey);
    try {
      if (runtime != null) {
        var response = runtime.onRuntimeDetached(instanceKey);
        if (response != null && !response.isSuccess()) {
          LOG.warn("Pipeline element {} did not stop cleanly: {}", instanceKey, response.getOptionalMessage());
        }
      }
    } catch (RuntimeException e) {
      LOG.warn("Pipeline element {} threw an exception while stopping", instanceKey, e);
    } finally {
      runningInstances.remove(instanceKey);
    }
  }

  private void stopAdapters() {
    List<AdapterDescription> adapters = List.copyOf(adapterManagement.getAllRunningAdapterInstances());
    LOG.info("Stopping {} running adapters...", adapters.size());
    adapters.forEach(adapter -> {
      try {
        adapterManagement.stopAdapter(adapter);
      } catch (AdapterException | RuntimeException e) {
        LOG.warn("Adapter {} threw an exception while stopping", adapter.getElementId(), e);
      }
    });
  }

  private void stopFunctions() {
    FunctionsShutdownResponse response;
    try {
      response = functionShutdown.get();
    } catch (RuntimeException e) {
      LOG.warn("Functions threw an exception while stopping", e);
      return;
    }
    var statefulFunctions = response.getFunctions().stream()
        .filter(result -> result.getState() != null)
        .toList();
    if (statefulFunctions.isEmpty()) {
      return;
    }
    LOG.info("Persisting state of {} functions...", statefulFunctions.size());
    StreamPipesClient client;
    try {
      client = clientSupplier.get();
    } catch (RuntimeException e) {
      LOG.warn("Could not persist function state, the core might already be down: {}", e.getMessage());
      return;
    }
    for (int i = 0; i < statefulFunctions.size(); i++) {
      if (!persistFunctionState(client, statefulFunctions.get(i))) {
        LOG.warn("The core is unreachable, skipping state persistence of {} remaining functions",
            statefulFunctions.size() - i - 1);
        return;
      }
    }
  }

  /**
   * @return false if the core could not be reached at all, so that the caller does not wait
   *         for a connection timeout per remaining function
   */
  private boolean persistFunctionState(StreamPipesClient client, FunctionShutdownResult result) {
    try {
      client.adminApi().persistFunctionState(result.getFunctionId(), result.getState());
      return true;
    } catch (SpHttpErrorStatusCode e) {
      if (e.getHttpStatusCode() != null && e.getHttpStatusCode() == 404) {
        LOG.warn("Could not persist state of function {}: the core's function state endpoints are disabled "
            + "(set SP_FUNCTION_STATE_ENDPOINTS_ENABLED=true)", result.getFunctionId());
      } else {
        LOG.warn("Could not persist state of function {}: core answered with status {}",
            result.getFunctionId(), e.getHttpStatusCode());
      }
      return true;
    } catch (RuntimeException e) {
      LOG.warn("Could not persist state of function {}: {}", result.getFunctionId(), e.getMessage());
      return false;
    }
  }
}
