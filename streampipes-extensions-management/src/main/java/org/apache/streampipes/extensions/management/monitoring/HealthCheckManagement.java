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

package org.apache.streampipes.extensions.management.monitoring;

import org.apache.streampipes.commons.constants.InstanceIdExtractor;
import org.apache.streampipes.extensions.management.connect.AdapterWorkerManagement;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.init.RunningAdapterInstances;
import org.apache.streampipes.extensions.management.init.RunningInstances;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.health.ExtensionInstanceHealth;

import java.util.stream.Collectors;

public class HealthCheckManagement {

  private final AdapterWorkerManagement adapterManagement;
  private final RunningInstances runningInstances;

  public HealthCheckManagement() {
    this(new AdapterWorkerManagement(
             RunningAdapterInstances.INSTANCE,
             DeclarersSingleton.getInstance()
         ),
         RunningInstances.INSTANCE);
  }

  public HealthCheckManagement(AdapterWorkerManagement adapterManagement,
                               RunningInstances runningInstances) {
    this.adapterManagement = adapterManagement;
    this.runningInstances = runningInstances;
  }

  public ExtensionInstanceHealth getExtensionInstanceHealth() {
    var runningAdapterInstances = adapterManagement.getAllRunningAdapterInstances()
        .stream()
        .map(NamedStreamPipesEntity::getElementId)
        .collect(Collectors.toSet());

    var runningPipelineElementInstances = runningInstances.getRunningInstanceIds()
        .stream()
        .map(InstanceIdExtractor::extractId)
        .collect(Collectors.toSet());

    return new ExtensionInstanceHealth(
        runningAdapterInstances,
        runningPipelineElementInstances
    );
  }
}
