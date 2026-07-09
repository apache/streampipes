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

import org.apache.streampipes.commons.constants.InstanceIdExtractor;
import org.apache.streampipes.health.monitoring.model.ActiveResources;
import org.apache.streampipes.health.monitoring.model.RunningAdapterInstance;
import org.apache.streampipes.health.monitoring.model.RunningExtensionInstances;
import org.apache.streampipes.health.monitoring.model.RunningPipelineElementInstance;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.health.ExtensionInstanceHealth;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExtensionInstanceDetailsProvider {

  private final IExtensionsServiceStorage extensionsServiceStorage;
  private final ResourceProvider resourceProvider;
  private final ExtensionServiceRequestManager extensionRequestManager;
  private final SpResourceManager resourceManager;

  public ExtensionInstanceDetailsProvider(IExtensionsServiceStorage extensionsServiceStorage,
                                          ResourceProvider resourceProvider,
                                          ExtensionServiceRequestManager extensionRequestManager,
                                          SpResourceManager resourceManager) {
    this.extensionsServiceStorage = extensionsServiceStorage;
    this.resourceProvider = resourceProvider;
    this.extensionRequestManager = extensionRequestManager;
    this.resourceManager = resourceManager;
  }

  public RunningExtensionInstances getRunningInstances(String serviceId) {
    var extensionHealth = new ExtensionInstanceAvailabilityCheck(
        extensionsServiceStorage,
        serviceId,
        extensionRequestManager,
        resourceManager
    ).checkRunningInstances();

    var activeResources = resourceProvider.loadActiveResources();
    return new RunningExtensionInstances(
        serviceId,
        getRunningAdapters(serviceId, extensionHealth, activeResources),
        getRunningPipelineElements(serviceId, extensionHealth, activeResources)
    );
  }

  private List<RunningAdapterInstance> getRunningAdapters(String serviceId,
                                                          ExtensionInstanceHealth extensionHealth,
                                                          ActiveResources activeResources) {
    Map<String, AdapterDescription> adaptersByInstanceId = activeResources.allAdapters()
        .stream()
        .filter(adapter -> serviceId.equals(adapter.getSelectedServiceId()))
        .collect(Collectors.toMap(
            AdapterDescription::getElementId,
            adapter -> adapter,
            (existing, replacement) -> existing
        ));

    return extensionHealth.adapterInstanceStates()
        .entrySet()
        .stream()
        .map(entry -> {
          var adapter = adaptersByInstanceId.get(entry.getKey());
          return new RunningAdapterInstance(
              entry.getKey(),
              adapter != null ? adapter.getName() : null,
              adapter != null ? adapter.getAppId() : null,
              entry.getValue(),
              adapter == null
          );
        })
        .sorted(Comparator
            .comparing(RunningAdapterInstance::orphaned).reversed()
            .thenComparing(RunningAdapterInstance::name,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
            .thenComparing(RunningAdapterInstance::instanceId))
        .toList();
  }

  private List<RunningPipelineElementInstance> getRunningPipelineElements(String serviceId,
                                                                          ExtensionInstanceHealth extensionHealth,
                                                                          ActiveResources activeResources) {
    Map<String, RunningPipelineElementInstance> pipelineElementsByInstanceId = activeResources.runningPipelines()
        .stream()
        .flatMap(pipeline -> runningPipelineElements(pipeline)
            .filter(element -> serviceId.equals(element.getSelectedServiceId()))
            .map(element -> new RunningPipelineElementInstance(
                InstanceIdExtractor.extractId(element.getElementId()),
                element.getName(),
                element.getAppId(),
                pipeline.getPipelineId(),
                pipeline.getName(),
                getPipelineElementType(element),
                false
            )))
        .collect(Collectors.toMap(
            RunningPipelineElementInstance::instanceId,
            element -> element,
            (existing, replacement) -> existing
        ));

    return extensionHealth.runningPipelineElementInstanceIds()
        .stream()
        .map(instanceId -> pipelineElementsByInstanceId.getOrDefault(
            instanceId,
            new RunningPipelineElementInstance(
                instanceId,
                null,
                null,
                null,
                null,
                "UNKNOWN",
                true
            )
        ))
        .sorted(Comparator
            .comparing(RunningPipelineElementInstance::orphaned).reversed()
            .thenComparing(RunningPipelineElementInstance::pipelineName,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
            .thenComparing(RunningPipelineElementInstance::name,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
            .thenComparing(RunningPipelineElementInstance::instanceId))
        .toList();
  }

  private Stream<InvocableStreamPipesEntity> runningPipelineElements(Pipeline pipeline) {
    return Stream.concat(
        pipeline.getSepas().stream(),
        pipeline.getActions().stream()
    );
  }

  private String getPipelineElementType(InvocableStreamPipesEntity element) {
    if (element instanceof DataProcessorInvocation) {
      return "PROCESSOR";
    } else if (element instanceof DataSinkInvocation) {
      return "SINK";
    } else {
      return "UNKNOWN";
    }
  }
}
