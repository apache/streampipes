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
import org.apache.streampipes.extensions.management.connect.AdapterTransitionRegistry;
import org.apache.streampipes.extensions.management.connect.AdapterWorkerManagement;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.init.RunningAdapterInstances;
import org.apache.streampipes.extensions.management.init.RunningInstances;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.health.AdapterInstanceState;
import org.apache.streampipes.model.health.ExtensionInstanceHealth;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HealthCheckManagement {

  private final AdapterWorkerManagement adapterManagement;
  private final RunningInstances runningInstances;
  private final AdapterTransitionRegistry adapterTransitionRegistry;

  public HealthCheckManagement() {
    this(AdapterTransitionRegistry.INSTANCE);
  }

  public HealthCheckManagement(AdapterTransitionRegistry adapterTransitionRegistry) {
    this(new AdapterWorkerManagement(
             RunningAdapterInstances.INSTANCE,
             DeclarersSingleton.getInstance(),
             adapterTransitionRegistry
         ),
         RunningInstances.INSTANCE,
         adapterTransitionRegistry);
  }

  public HealthCheckManagement(AdapterWorkerManagement adapterManagement,
                               RunningInstances runningInstances,
                               AdapterTransitionRegistry adapterTransitionRegistry) {
    this.adapterManagement = adapterManagement;
    this.runningInstances = runningInstances;
    this.adapterTransitionRegistry = adapterTransitionRegistry;
  }

  public ExtensionInstanceHealth getExtensionInstanceHealth() {
    return new ExtensionInstanceHealth(
        getAdapterInstanceStates(),
        getRunningPipelineElementInstanceIds()
    );
  }

  private Map<String, AdapterInstanceState> getAdapterInstanceStates() {
    var adapterInstanceStates = getRunningAdapterInstanceStates();
    setTransitioningAdapterInstanceStates(adapterInstanceStates);
    return adapterInstanceStates;
  }

  private Map<String, AdapterInstanceState> getRunningAdapterInstanceStates() {
    return adapterManagement.getAllRunningAdapterInstances()
        .stream()
        .map(NamedStreamPipesEntity::getElementId)
        .collect(Collectors.toMap(
            adapterInstanceId -> adapterInstanceId,
            adapterInstanceId -> AdapterInstanceState.RUNNING,
            (existingState, replacementState) -> existingState
        ));
  }

  private void setTransitioningAdapterInstanceStates(Map<String, AdapterInstanceState> adapterInstanceStates) {
    adapterInstanceStates.putAll(adapterTransitionRegistry.getTransitioningAdapterInstanceStates());
  }

  private Set<String> getRunningPipelineElementInstanceIds() {
    return runningInstances.getRunningInstanceIds()
        .stream()
        .map(InstanceIdExtractor::extractId)
        .collect(Collectors.toSet());
  }
}
