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

package org.apache.streampipes.service.extensions.function;

import org.apache.streampipes.extensions.api.declarer.IStreamPipesFunctionDeclarer;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataStream;
import org.apache.streampipes.extensions.api.pe.config.IDataStreamConfiguration;
import org.apache.streampipes.extensions.management.client.StreamPipesClientResolver;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.model.function.FunctionDefinition;
import org.apache.streampipes.model.function.FunctionShutdownResult;
import org.apache.streampipes.model.function.FunctionsShutdownResponse;
import org.apache.streampipes.sdk.builder.stream.DataStreamConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum StreamPipesFunctionHandler {

  INSTANCE;

  private static final Logger LOG = LoggerFactory.getLogger(StreamPipesFunctionHandler.class);

  private final Map<String, IStreamPipesFunctionDeclarer> runningInstances;

  StreamPipesFunctionHandler() {
    this.runningInstances = new HashMap<>();
  }

  public void initializeFunctions(String serviceId) {
    initializeFunctions(DeclarersSingleton.getInstance().getFunctions().values(), serviceId);
  }

  public void initializeFunctions(Collection<IStreamPipesFunctionDeclarer> functions,
                                  String serviceGroup) {
    registerDataStreams(functions);
    functions.forEach(function -> {
      function.invokeRuntime(serviceGroup);
      runningInstances.put(function.getFunctionConfig().getFunctionId().getId(), function);
    });
    if (!(runningInstances.isEmpty())) {
      new Thread(new FunctionRegistrationHandler(getFunctionDefinitions())).start();
    }
  }

  public void registerDataStreams(Collection<IStreamPipesFunctionDeclarer> functions) {
    var client = new StreamPipesClientResolver().makeStreamPipesClientInstance();
    functions.forEach(function -> {
      function.getFunctionConfig().getOutputDataStreams().values().forEach(ds -> {
        if (!DeclarersSingleton.getInstance().getDataStreams().containsKey(ds.getAppId())) {
          DeclarersSingleton.getInstance().add(new IStreamPipesDataStream() {
            @Override
            public IDataStreamConfiguration declareConfig() {
              return DataStreamConfiguration.create(
                  () -> this,
                  ds
              );
            }

            @Override
            public void executeStream() {

            }

            @Override
            public boolean isExecutable() {
              return false;
            }
          });
          if (client.streams().get(ds.getElementId()).isEmpty()) {
            client.streams().create(ds);
          } else {
            client.streams().update(ds);
          }
        }
      });
    });
  }

  /**
   * Discards every function and collects its registered state. A function whose stop hook
   * fails is logged and skipped; the other functions are still discarded and their state is
   * still returned, since this is the only path that persists it on shutdown.
   */
  public FunctionsShutdownResponse shutdownFunctionsAndGetState() {
    var shutdownResults = new ArrayList<FunctionShutdownResult>();
    this.runningInstances.forEach((key, value) -> {
      try {
        value.discardRuntime();
      } catch (RuntimeException e) {
        LOG.warn("Function {} threw an exception while stopping, its state is persisted anyway", key, e);
      }
      try {
        shutdownResults.add(new FunctionShutdownResult(key, value.getRegisteredStatePayload().orElse(null)));
      } catch (RuntimeException e) {
        LOG.warn("Could not read the state of function {}", key, e);
      }
    });
    this.runningInstances.clear();

    return new FunctionsShutdownResponse(shutdownResults);
  }

  private List<FunctionDefinition> getFunctionDefinitions() {
    return this.runningInstances
        .values()
        .stream()
        .map(function -> new FunctionDefinition(
            function.getFunctionConfig().getFunctionId(),
            function.requiredStreamIds()))
        .collect(Collectors.toList());
  }

}
