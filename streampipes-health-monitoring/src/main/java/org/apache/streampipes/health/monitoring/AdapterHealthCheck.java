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
package org.apache.streampipes.health.monitoring;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.prometheus.adapter.AdapterMetrics;
import org.apache.streampipes.commons.prometheus.adapter.AdapterMetricsManager;
import org.apache.streampipes.health.monitoring.model.HealthCheckData;
import org.apache.streampipes.loadbalance.pipeline.ExtensionsLogProvider;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.health.AdapterInstanceState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

public class AdapterHealthCheck implements HealthCheck {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterHealthCheck.class);

  private final HealthCheckData healthCheckData;

  public AdapterHealthCheck(HealthCheckData healthCheckData) {
    this.healthCheckData = healthCheckData;
  }

  /**
   * In this method it is checked which adapters are currently running. Then it calls all workers to
   * validate if the adapter instance is still running as expected. If the adapter is not running
   * anymore a new worker instance is invoked. In addition, it publishes monitoring metrics for all
   * running adapters (in line with
   * {@link PipelineHealthCheck}).
   */
  @Override
  public void runCheck() {
    LOG.debug("Adapter health check started");

    try {
      if (!healthCheckData.activeResources().runningAdapters().isEmpty()) {
        var allAdaptersToRecover = this.getAdaptersToRecover();

        allAdaptersToRecover
            .forEach(adapter ->
                LOG.info("Adapter instance with id {} needs to be recovered", adapter.getElementId()));
        // Filter adapters so that only healthy and running adapters are updated in the metrics
        // endpoint
        var adaptersToMonitor = healthCheckData.activeResources().runningAdapters().stream()
            .filter(entry -> allAdaptersToRecover
                .stream()
                .noneMatch(r -> r.getElementId().equals(entry.getElementId()))
            )
            .toList();

        if (!adaptersToMonitor.isEmpty()) {
          updateMonitoringMetrics(adaptersToMonitor);
        } else {
          LOG.debug("No running adapter instances to monitor.");
        }

        LOG.debug("Monitoring metrics updated for running adapters.");

        // Recover Adapters
        this.recoverAdapters(allAdaptersToRecover);
      }
    } catch (NoSuchElementException e) {
      LOG.error("Could not update adapter metrics due to an invalid state. ({})", e.getMessage());
    }
  }

  /**
   * Updates the monitoring metrics based on the descriptions of running adapters.
   *
   * @param runningAdapterDescriptions A map containing the descriptions of running adapters, where
   *        the key is the adapter's element ID and the value is the corresponding adapter
   *        description.
   */
  protected void updateMonitoringMetrics(List<AdapterDescription> runningAdapterDescriptions) {

    var adapterMetrics = AdapterMetricsManager.getInstance().getAdapterMetrics();
    runningAdapterDescriptions
        .forEach(adapterDescription -> updateTotalEventsPublished(adapterMetrics,
                                                                  adapterDescription.getElementId(),
                                                                  adapterDescription.getName()));
    LOG.debug("Monitoring {} adapter instances", adapterMetrics.size());
  }

  private void updateTotalEventsPublished(AdapterMetrics adapterMetrics, String adapterId,
                                          String adapterName) {

    // Check if the adapter is already registered; if not, register it first.
    // This step is crucial, especially when the StreamPipes Core service is restarted,
    // and there are existing running adapters that need proper registration.
    // Note: Proper registration is usually handled during the initial start of the adapter.
    if (!adapterMetrics.contains(adapterId)) {
      adapterMetrics.register(adapterId, adapterName);
    }

    adapterMetrics.updateTotalEventsPublished(adapterId, adapterName, ExtensionsLogProvider.INSTANCE
        .getMetricInfosForResource(adapterId).getMessagesOut().getCounter());
  }


  /**
   * Retrieves a list of adapters to recover by comparing the provided groupings of adapter instances
   * with the instances supposed to run according to the storage. For every adapter instance it is
   * verified that it actually runs on a worker node. If this is not the case, it is added to the
   * output of adapters to recover.
   *
   *        running.
   * @return A new map containing adapter instances to recover, filtered based on running instances.
   */
  public List<AdapterDescription> getAdaptersToRecover() {

    var adapterInstanceStates =
        healthCheckData.activeExtensionInstances()
            .values()
            .stream()
            .flatMap(h -> adapterInstanceStates(h.adapterInstanceStates()).entrySet().stream())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (existingState, replacementState) -> existingState
            ));

    return healthCheckData.activeResources()
        .runningAdapters()
        .stream()
        .filter(Objects::nonNull)
        .filter(a -> a.getElementId() != null)
        .filter(a -> !adapterInstanceStates.containsKey(a.getElementId()))
        .toList();
  }

  private Map<String, AdapterInstanceState> adapterInstanceStates(
      Map<String, AdapterInstanceState> adapterInstanceStates) {
    return adapterInstanceStates == null ? Map.of() : adapterInstanceStates;
  }

  public void recoverAdapters(List<AdapterDescription> adaptersToRecover) {
    for (AdapterDescription adapterDescription : adaptersToRecover) {
      // Invoke all adapters that were running when the adapter container was stopped
      try {
        if (adapterDescription.isRunning()) {
          LOG.debug("Start recovering adapter {} ", adapterDescription.getElementId());
          this.healthCheckData.resourceProvider().adapterMasterManagement().startStreamAdapter(adapterDescription.getElementId());
          LOG.info("Adapter {} is recovered", adapterDescription.getElementId());
        }
      } catch (AdapterException e) {
        LOG.warn("Could not start adapter {} ({})", adapterDescription.getName(), e.getMessage());
      } catch (Exception e) {
        LOG.error(
            "Unexpected error while recovering adapter {} ({})",
            adapterDescription.getName(),
            e.getMessage()
        );
      }
    }
  }
}
