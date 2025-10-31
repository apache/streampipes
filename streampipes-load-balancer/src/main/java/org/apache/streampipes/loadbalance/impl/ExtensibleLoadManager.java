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
package org.apache.streampipes.loadbalance.impl;

import org.apache.streampipes.loadbalance.ExtensionServiceSelector;
import org.apache.streampipes.loadbalance.LoadBalancer;
import org.apache.streampipes.loadbalance.PipelineMigrator;
import org.apache.streampipes.loadbalance.unit.ResourceUnitStatsScanner;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceStatus;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.List;

public class ExtensibleLoadManager implements LoadBalancer {

    ExtensionServiceSelector selector;

  PipelineMigrator migrator;

  public ExtensibleLoadManager(ExtensionServiceSelector selector,
                               PipelineMigrator pipelineMigrator) {
    this.selector = selector;
    this.migrator = pipelineMigrator;
  }

  @Override
  public SpServiceRegistration allocation(List<SpServiceRegistration> serviceRegistrations,
                                          List<String> labels) {
    return selector.select(serviceRegistrations, labels);
  }

  public void doLoadShedding() {
    CRUDStorage<SpServiceRegistration> storage =  StorageDispatcher.INSTANCE.getNoSqlStore().getExtensionsServiceStorage();
    List<SpServiceRegistration> services = storage.findAll().stream().filter(s -> s.getStatus() == SpServiceStatus.HEALTHY)
            .toList();

    // Collect load balancer metrics for all services using optimized database queries
    ResourceUnitStatsScanner.collectAllLoadBalancerMetrics();

    // Perform load shedding
    migrator.doLoadShedding(services);
  }
}
