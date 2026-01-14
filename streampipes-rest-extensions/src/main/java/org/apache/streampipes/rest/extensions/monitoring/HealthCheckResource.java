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

package org.apache.streampipes.rest.extensions.monitoring;

import org.apache.streampipes.commons.constants.InstanceIdExtractor;
import org.apache.streampipes.extensions.management.connect.AdapterWorkerManagement;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.init.RunningAdapterInstances;
import org.apache.streampipes.extensions.management.init.RunningInstances;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.health.ExtensionInstanceHealth;
import org.apache.streampipes.rest.extensions.AbstractExtensionsResource;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("health")
public class HealthCheckResource extends AbstractExtensionsResource {

  private final AdapterWorkerManagement adapterManagement = new AdapterWorkerManagement(
      RunningAdapterInstances.INSTANCE,
      DeclarersSingleton.getInstance()
  );

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ExtensionInstanceHealth> getRunningInstances() {

    var runningAdapterInstances = adapterManagement.getAllRunningAdapterInstances()
        .stream()
        .map(NamedStreamPipesEntity::getElementId)
        .collect(Collectors.toSet());

    var runningPipelineElementInstances = RunningInstances.INSTANCE.getRunningInstanceIds()
        .stream()
        .map(InstanceIdExtractor::extractId)
        .collect(Collectors.toSet());

    var instanceHealth = new ExtensionInstanceHealth(
        runningAdapterInstances,
        runningPipelineElementInstances
    );

    return ok(instanceHealth);
  }
}
