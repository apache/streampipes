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

package org.apache.streampipes.connect.management.health;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.connect.management.management.AdapterMasterManagement;
import org.apache.streampipes.connect.management.management.WorkerRestClient;
import org.apache.streampipes.connect.management.util.WorkerPaths;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.couchdb.CouchDbStorageManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterHealthCheck {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterHealthCheck.class);

  private final IAdapterStorage adapterStorage;
  private final AdapterMasterManagement adapterMasterManagement;

  public AdapterHealthCheck() {
    this.adapterStorage = CouchDbStorageManager.INSTANCE.getAdapterInstanceStorage();
    this.adapterMasterManagement = new AdapterMasterManagement();
  }

  public AdapterHealthCheck(IAdapterStorage adapterStorage,
                            AdapterMasterManagement adapterMasterManagement) {
    this.adapterStorage = adapterStorage;
    this.adapterMasterManagement = adapterMasterManagement;
  }

  /**
   * In this method it is checked which adapters are currently running.
   * Then it calls all workers to validate if the adapter instance is
   * still running as expected. If the adapter is not running anymore a new worker instance is invoked.
   */
  public void checkAndRestoreAdapters() {
    // Get all adapters
    Map<String, AdapterDescription> allRunningInstancesAdapterDescriptions =
        this.getAllRunningInstancesAdapterDescriptions();

    // Get all worker containers that run adapters
    Map<String, List<AdapterDescription>> groupByWorker =
        this.getAllWorkersWithAdapters(allRunningInstancesAdapterDescriptions);

    // Get adapters that are not running anymore
    Map<String, AdapterDescription> allAdaptersToRecover =
        this.getAdaptersToRecover(groupByWorker, allRunningInstancesAdapterDescriptions);

    // Recover Adapters
    this.recoverAdapters(allAdaptersToRecover);
  }

  public Map<String, AdapterDescription> getAllRunningInstancesAdapterDescriptions() {
    Map<String, AdapterDescription> result = new HashMap<>();
    List<AdapterDescription> allRunningInstancesAdapterDescription = this.adapterStorage.getAllAdapters();
    allRunningInstancesAdapterDescription.forEach(adapterDescription ->
        result.put(adapterDescription.getElementId(), adapterDescription));

    return result;
  }

  public Map<String, List<AdapterDescription>> getAllWorkersWithAdapters(
      Map<String, AdapterDescription> allRunningInstancesAdapterDescription) {

    Map<String, List<AdapterDescription>> groupByWorker = new HashMap<>();
    allRunningInstancesAdapterDescription.values().forEach(ad -> {
      String selectedEndpointUrl = ad.getSelectedEndpointUrl();
      if (selectedEndpointUrl != null) {
        if (groupByWorker.containsKey(selectedEndpointUrl)) {
          groupByWorker.get(selectedEndpointUrl).add(ad);
        } else {
          List<AdapterDescription> tmp = new ArrayList<>();
          tmp.add(ad);
          groupByWorker.put(selectedEndpointUrl, tmp);
        }
      }
    });

    return groupByWorker;
  }

  public Map<String, AdapterDescription> getAdaptersToRecover(
      Map<String, List<AdapterDescription>> groupByWorker,
      Map<String, AdapterDescription> allRunningInstancesAdapterDescription) {
    groupByWorker.keySet().forEach(adapterEndpointUrl -> {
      try {
        List<AdapterDescription> allRunningInstancesOfOneWorker =
            WorkerRestClient.getAllRunningAdapterInstanceDescriptions(
                adapterEndpointUrl + WorkerPaths.getRunningAdaptersPath());
        allRunningInstancesOfOneWorker.forEach(adapterDescription ->
            allRunningInstancesAdapterDescription.remove(adapterDescription.getElementId()));
      } catch (AdapterException e) {
        e.printStackTrace();
      }
    });

    return allRunningInstancesAdapterDescription;
  }


  public void recoverAdapters(Map<String, AdapterDescription> adaptersToRecover) {
    for (AdapterDescription adapterDescription : adaptersToRecover.values()) {
      // Invoke all adapters that were running when the adapter container was stopped
      try {
        if (adapterDescription.isRunning()) {
          this.adapterMasterManagement.startStreamAdapter(adapterDescription.getElementId());
        }
      } catch (AdapterException e) {
        LOG.warn("Could not start adapter {}", adapterDescription.getName(), e);
      }
    }

  }

}
