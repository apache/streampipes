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
import org.apache.streampipes.commons.prometheus.adapter.AdapterMetrics;
import org.apache.streampipes.commons.prometheus.adapter.AdapterMetricsManager;
import org.apache.streampipes.connect.management.management.AdapterMasterManagement;
import org.apache.streampipes.connect.management.management.WorkerRestClient;
import org.apache.streampipes.connect.management.util.WorkerPaths;
import org.apache.streampipes.manager.monitoring.pipeline.ExtensionsLogProvider;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.storage.api.IAdapterStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class AdapterHealthCheck implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterHealthCheck.class);

  private final IAdapterStorage adapterStorage;
  private final AdapterMasterManagement adapterMasterManagement;

  public AdapterHealthCheck(
      IAdapterStorage adapterStorage,
      AdapterMasterManagement adapterMasterManagement
  ) {
    this.adapterStorage = adapterStorage;
    this.adapterMasterManagement = adapterMasterManagement;
  }

  @Override
  public void run() {
    this.checkAndRestoreAdapters();
  }

  /**
   * In this method it is checked which adapters are currently running.
   * Then it calls all workers to validate if the adapter instance is
   * still running as expected. If the adapter is not running anymore a new worker instance is invoked.
   * In addition, it publishes monitoring metrics for all running adapters (in line with
   * {@link org.apache.streampipes.manager.health.PipelineHealthCheck}).
   */
  public void checkAndRestoreAdapters() {
    // Get all adapters that are supposed to run according to the backend storage
    Map<String, AdapterDescription> adapterInstancesSupposedToRun =
        this.getAllAdaptersSupposedToRun();

    // group all adapter instances supposed to run by their worker service URL
    Map<String, List<AdapterDescription>> groupByWorker =
        this.getAllWorkersWithAdapters(adapterInstancesSupposedToRun);

    // Get adapters that are not running anymore
    Map<String, AdapterDescription> allAdaptersToRecover =
        this.getAdaptersToRecover(groupByWorker, adapterInstancesSupposedToRun);

    try {
      if (!adapterInstancesSupposedToRun.isEmpty()) {
        // Filter adapters so that only healthy and running adapters are updated in the metrics endpoint
        var adaptersToMonitor = adapterInstancesSupposedToRun
            .entrySet()
            .stream()
            .filter((entry -> !allAdaptersToRecover.containsKey(entry.getKey())))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (!adaptersToMonitor.isEmpty()) {
          updateMonitoringMetrics(adaptersToMonitor);
        } else {
          LOG.info("No running adapter instances to monitor.");
        }
      }
    } catch (NoSuchElementException e) {
      LOG.error("Could not update adapter metrics due to an invalid state. ({})", e.getMessage());
    }

    // Recover Adapters
    this.recoverAdapters(allAdaptersToRecover);
  }

  /**
   * Updates the monitoring metrics based on the descriptions of running adapters.
   *
   * @param runningAdapterDescriptions A map containing the descriptions of running adapters, where the key is the
   *                                   adapter's element ID and the value is the corresponding adapter description.
   */
  protected void updateMonitoringMetrics(Map<String, AdapterDescription> runningAdapterDescriptions) {

    var adapterMetrics = AdapterMetricsManager.getInstance()
                                              .getAdapterMetrics();
    runningAdapterDescriptions.values()
                              .forEach(
                                  adapterDescription -> updateTotalEventsPublished(
                                      adapterMetrics,
                                      adapterDescription.getElementId(),
                                      adapterDescription.getName()
                                  ));
    LOG.info("Monitoring {} adapter instances", adapterMetrics.size());
  }

  private void updateTotalEventsPublished(AdapterMetrics adapterMetrics, String adapterId, String adapterName) {

    // Check if the adapter is already registered; if not, register it first.
    // This step is crucial, especially when the StreamPipes Core service is restarted,
    // and there are existing running adapters that need proper registration.
    // Note: Proper registration is usually handled during the initial start of the adapter.
    if (!adapterMetrics.contains(adapterId)) {
      adapterMetrics.register(adapterId, adapterName);
    }

    adapterMetrics.updateTotalEventsPublished(
        adapterId,
        adapterName,
        ExtensionsLogProvider.INSTANCE.getMetricInfosForResource(adapterId)
                                      .getMessagesOut()
                                      .getCounter()
    );
  }


  /**
   * Retrieves a map of all adapter instances that are supposed to be running according to the backend storage.
   * <p>
   * This method queries the adapter storage to obtain information about all adapters
   * and filters the running instances. The resulting map is keyed by the element ID
   * of each running adapter, and the corresponding values are the respective
   * {@link AdapterDescription} objects.
   *
   * @return A map containing all adapter instances supposed to be running according to the backend storage.
   * The keys are element IDs, and the values are the corresponding adapter descriptions.
   */
  public Map<String, AdapterDescription> getAllAdaptersSupposedToRun() {
    Map<String, AdapterDescription> result = new HashMap<>();
    List<AdapterDescription> allRunningInstancesAdapterDescription = this.adapterStorage.findAll();
    allRunningInstancesAdapterDescription
        .stream()
        .filter(AdapterDescription::isRunning)
        .forEach(adapterDescription ->
                     result.put(
                         adapterDescription.getElementId(),
                         adapterDescription
                     ));

    return result;
  }

  public Map<String, List<AdapterDescription>> getAllWorkersWithAdapters(
      Map<String, AdapterDescription> adapterInstancesSupposedToRun
  ) {

    Map<String, List<AdapterDescription>> groupByWorker = new HashMap<>();
    adapterInstancesSupposedToRun.values()
                                 .forEach(ad -> {
                                   String selectedEndpointUrl = ad.getSelectedEndpointUrl();
                                   if (selectedEndpointUrl != null) {
                                     if (groupByWorker.containsKey(selectedEndpointUrl)) {
                                       groupByWorker.get(selectedEndpointUrl)
                                                    .add(ad);
                                     } else {
                                       List<AdapterDescription> adapterDescriptionsPerWorker = new ArrayList<>();
                                       adapterDescriptionsPerWorker.add(ad);
                                       groupByWorker.put(selectedEndpointUrl, adapterDescriptionsPerWorker);
                                     }
                                   }
                                 });

    return groupByWorker;
  }

  /**
   * Retrieves a map of adapters to recover by comparing the provided groupings of adapter instances
   * with the instances supposed to run according to the storage.
   * For every adapter instance it is verified that it actually runs on a worker node.
   * If this is not the case, it is added to the output of adapters to recover.
   *
   * @param adapterInstancesGroupedByWorker A map grouping adapter instances by worker.
   * @param adapterInstancesSupposedToRun   The map containing all adapter instances supposed to be running.
   * @return A new map containing adapter instances to recover, filtered based on running instances.
   */
  public Map<String, AdapterDescription> getAdaptersToRecover(
      Map<String, List<AdapterDescription>> adapterInstancesGroupedByWorker,
      Map<String, AdapterDescription> adapterInstancesSupposedToRun
  ) {

    // NOTE: This line is added to prevent modifying the existing map of instances supposed to run
    // It looks like the parameter `adapterInstancesSupposedToRun` is not required at all,
    // but this should be checked more carefully.
    Map<String, AdapterDescription> adaptersToRecover = new HashMap<>(adapterInstancesSupposedToRun);

    adapterInstancesGroupedByWorker.keySet()
                                   .forEach(adapterEndpointUrl -> {
                                     try {
                                       List<AdapterDescription> allRunningInstancesOfOneWorker =
                                           WorkerRestClient.getAllRunningAdapterInstanceDescriptions(
                                               adapterEndpointUrl + WorkerPaths.getRunningAdaptersPath());

                                       // only keep adapters where there is no running adapter instance
                                       // therefore, all others are removed
                                       allRunningInstancesOfOneWorker.forEach(
                                           adapterDescription ->
                                               adaptersToRecover.remove(
                                                   adapterDescription.getElementId()));
                                     } catch (AdapterException e) {
                                       LOG.info(
                                           "Could not recover adapter at endpoint {}  - "
                                               + "marking it as requested to recover (reason: {})",
                                           adapterEndpointUrl,
                                           e.getMessage()
                                       );
                                     }
                                   });

    return adaptersToRecover;
  }


  public void recoverAdapters(Map<String, AdapterDescription> adaptersToRecover) {
    for (AdapterDescription adapterDescription : adaptersToRecover.values()) {
      // Invoke all adapters that were running when the adapter container was stopped
      try {
        if (adapterDescription.isRunning()) {
          this.adapterMasterManagement.startStreamAdapter(adapterDescription.getElementId());
        }
      } catch (AdapterException e) {
        LOG.warn("Could not start adapter {} ({})", adapterDescription.getName(), e.getMessage());
      }
    }

  }
}
