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

import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.init.IDeclarersSingleton;
import org.apache.streampipes.extensions.management.init.RunningAdapterInstances;
import org.apache.streampipes.extensions.management.monitoring.SpMonitoringManager;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.sdk.extractor.AdapterParameterExtractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class AdapterWorkerManagement {


  private static final Logger LOG = LoggerFactory.getLogger(AdapterWorkerManagement.class);

  private RunningAdapterInstances runningAdapterInstances;
  private DeclarersSingleton declarers;

  public AdapterWorkerManagement(RunningAdapterInstances runningAdapterInstances,
                                 DeclarersSingleton declarers) {
    this.runningAdapterInstances = runningAdapterInstances;
    this.declarers = declarers;
  }

  public Collection<AdapterDescription> getAllRunningAdapterInstances() {
    return RunningAdapterInstances.INSTANCE.getAllRunningAdapterDescriptions();
  }

  public void invokeAdapter(AdapterDescription adapterDescription) throws AdapterException {
    var adapter = getDeclarerSingleton()
        .getAdapter(adapterDescription.getAppId());

    if (adapter.isPresent()) {

      runningAdapterInstances.addAdapter(
          adapterDescription.getElementId(),
          adapter.get(),
          adapterDescription);

      var extractor = AdapterParameterExtractor.from(adapterDescription);

      // TODO how to intantiate the collector?

      adapter.get().onAdapterStarted(extractor, null, null);
    } else {
      //TODO
    }
  }

  public void stopAdapter(AdapterDescription adapterDescription) throws AdapterException {

    String elementId = adapterDescription.getElementId();

//    IAdapter<?> adapter = RunningAdapterInstances.INSTANCE.removeAdapter(elementId);

//    if (adapter != null) {
//      adapter.stopAdapter();
//    }
    resetMonitoring(elementId);
  }

  private void resetMonitoring(String elementId) {
    SpMonitoringManager.INSTANCE.reset(elementId);
  }

  public IDeclarersSingleton getDeclarerSingleton() {
    return DeclarersSingleton.getInstance();
  }

}
