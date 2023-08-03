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

package org.apache.streampipes.extensions.management.connect;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.monitoring.SpMonitoringManager;
import org.apache.streampipes.extensions.management.connect.adapter.model.EventCollector;
import org.apache.streampipes.extensions.management.context.AdapterContextGenerator;
import org.apache.streampipes.extensions.management.init.IDeclarersSingleton;
import org.apache.streampipes.extensions.management.init.RunningAdapterInstances;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.sdk.extractor.AdapterParameterExtractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class AdapterWorkerManagement {


  private static final Logger LOG = LoggerFactory.getLogger(AdapterWorkerManagement.class);

  private final RunningAdapterInstances runningAdapterInstances;
  private final IDeclarersSingleton declarers;

  public AdapterWorkerManagement(RunningAdapterInstances runningAdapterInstances,
                                 IDeclarersSingleton declarers) {
    this.runningAdapterInstances = runningAdapterInstances;
    this.declarers = declarers;
  }

  public Collection<AdapterDescription> getAllRunningAdapterInstances() {
    return runningAdapterInstances.getAllRunningAdapterDescriptions();
  }

  public void invokeAdapter(AdapterDescription adapterDescription) throws AdapterException {
    var adapter = declarers
        .getAdapter(adapterDescription.getAppId());

    if (adapter.isPresent()) {
      var newAdapterInstance = adapter.get().declareConfig().getSupplier().get();
      runningAdapterInstances.addAdapter(
          adapterDescription.getElementId(),
          newAdapterInstance,
          adapterDescription);

      var registeredParsers = newAdapterInstance.declareConfig().getSupportedParsers();
      var extractor = AdapterParameterExtractor.from(adapterDescription, registeredParsers);
      var eventCollector = EventCollector.from(adapterDescription);
      var runtimeContext = makeRuntimeContext(adapterDescription.getElementId());

      newAdapterInstance.onAdapterStarted(extractor, eventCollector, runtimeContext);
    } else {
      var errorMessage = "Adapter with id %s could not be found".formatted(adapterDescription.getAppId());
      LOG.error(errorMessage);
      throw new AdapterException(errorMessage);
    }
  }

  public void stopAdapter(AdapterDescription adapterDescription) throws AdapterException {

    String elementId = adapterDescription.getElementId();

    StreamPipesAdapter adapter = RunningAdapterInstances.INSTANCE.removeAdapter(elementId);

    if (adapter != null) {

      var registeredParsers = adapter.declareConfig().getSupportedParsers();
      var extractor = AdapterParameterExtractor.from(adapterDescription, registeredParsers);
      var runtimeContext = makeRuntimeContext(elementId);
      adapter.onAdapterStopped(extractor, runtimeContext);
    }

    resetMonitoring(elementId);
  }

  private IAdapterRuntimeContext makeRuntimeContext(String adapterInstanceId) {
    return new AdapterContextGenerator().makeRuntimeContext(adapterInstanceId);
  }

  private void resetMonitoring(String elementId) {
    SpMonitoringManager.INSTANCE.reset(elementId);
  }
}
