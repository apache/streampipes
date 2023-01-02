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

import org.apache.streampipes.extensions.api.connect.IAdapter;
import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.management.init.RunningAdapterInstances;
import org.apache.streampipes.extensions.management.monitoring.SpMonitoringManager;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.AdapterStreamDescription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class AdapterWorkerManagement {

  private static final Logger logger = LoggerFactory.getLogger(AdapterWorkerManagement.class);

  public Collection<AdapterDescription> getAllRunningAdapterInstances() {
    return RunningAdapterInstances.INSTANCE.getAllRunningAdapterDescriptions();
  }

  public void invokeStreamAdapter(AdapterStreamDescription adapterStreamDescription) throws AdapterException {

    IAdapter<?> adapter = AdapterUtils.setAdapter(adapterStreamDescription);

    RunningAdapterInstances.INSTANCE.addAdapter(adapterStreamDescription.getElementId(), adapter,
        adapterStreamDescription);
    adapter.startAdapter();
  }

  public void stopStreamAdapter(AdapterStreamDescription adapterStreamDescription) throws AdapterException {
    stopAdapter(adapterStreamDescription);
  }

  public void invokeSetAdapter(AdapterSetDescription adapterSetDescription) throws AdapterException {

    IAdapter<?> adapter = AdapterUtils.setAdapter(adapterSetDescription);

    RunningAdapterInstances.INSTANCE.addAdapter(adapterSetDescription.getElementId(), adapter, adapterSetDescription);

    adapter.changeEventGrounding(adapterSetDescription.getDataSet().getEventGrounding().getTransportProtocol());

    // Start a thread to start a set adapter
    Runnable r = () -> {
      try {
        adapter.startAdapter();
      } catch (AdapterException e) {
        e.printStackTrace();
      }
    };

    new Thread(r).start();
  }

  public void stopSetAdapter(AdapterSetDescription adapterSetDescription) throws AdapterException {
    stopAdapter(adapterSetDescription);
  }

  private void stopAdapter(AdapterDescription adapterDescription) throws AdapterException {

    String elementId = adapterDescription.getElementId();

    IAdapter<?> adapter = RunningAdapterInstances.INSTANCE.removeAdapter(elementId);

    if (adapter != null) {
      adapter.stopAdapter();
    }
    resetMonitoring(elementId);
  }

  private void resetMonitoring(String elementId) {
    SpMonitoringManager.INSTANCE.reset(elementId);
  }

}
