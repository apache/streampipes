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

import org.apache.streampipes.health.monitoring.model.HealthCheckData;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.model.health.ExtensionInstanceHealth;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class ExtensionHealthCheck implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(ExtensionHealthCheck.class);

  private final ResourceProvider resourceProvider;
  private final ExtensionServiceRequestManager extensionRequestManager;
  private final IExtensionsServiceStorage extensionsServiceStorage;

  public ExtensionHealthCheck(ResourceProvider resourceProvider,
                              IExtensionsServiceStorage extensionsServiceStorage,
                              ExtensionServiceRequestManager extensionRequestManager) {
    this.resourceProvider = resourceProvider;
    this.extensionsServiceStorage = extensionsServiceStorage;
    this.extensionRequestManager = extensionRequestManager;
  }

  @Override
  public void run() {
    try {
      var activeResources = resourceProvider.loadActiveResources();
      var activeCoreInstances = resourceProvider.loadActiveInstances(activeResources);

      var activeExtensionInstances = new HashMap<String, ExtensionInstanceHealth>();
      activeCoreInstances.keySet().forEach(serviceId -> {
        activeExtensionInstances.put(
            serviceId,
            new ExtensionInstanceAvailabilityCheck(extensionsServiceStorage, serviceId, extensionRequestManager).checkRunningInstances()
        );
      });

      var healthCheckData = new HealthCheckData(resourceProvider, activeResources, activeCoreInstances, activeExtensionInstances);
      new PipelineHealthCheck(healthCheckData, extensionRequestManager).runCheck();
      new AdapterHealthCheck(healthCheckData).runCheck();
    } catch (Exception e) {
      LOG.warn("An unhandled error occurred while running health check.", e);
    }
  }
}
