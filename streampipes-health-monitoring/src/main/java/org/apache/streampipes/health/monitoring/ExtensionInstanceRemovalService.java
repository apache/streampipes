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
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTargets;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequests;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

public class ExtensionInstanceRemovalService {

  private final IExtensionsServiceStorage extensionsServiceStorage;
  private final ResourceProvider resourceProvider;
  private final ExtensionServiceRequestManager extensionRequestManager;
  private final SpResourceManager resourceManager;

  public ExtensionInstanceRemovalService(IExtensionsServiceStorage extensionsServiceStorage,
                                         ResourceProvider resourceProvider,
                                         ExtensionServiceRequestManager extensionRequestManager,
                                         SpResourceManager resourceManager) {
    this.extensionsServiceStorage = extensionsServiceStorage;
    this.resourceProvider = resourceProvider;
    this.extensionRequestManager = extensionRequestManager;
    this.resourceManager = resourceManager;
  }

  public void removeAllInstances(String serviceId) throws IOException {
    var runningInstances = new ExtensionInstanceDetailsProvider(
        extensionsServiceStorage,
        resourceProvider,
        extensionRequestManager,
        resourceManager
    ).getRunningInstances(serviceId);

    for (var adapter : runningInstances.adapters()) {
      removeAdapterInstance(serviceId, adapter.instanceId());
    }

    for (var pipelineElement : runningInstances.pipelineElements()) {
      removePipelineElementInstance(serviceId, pipelineElement.instanceId());
    }
  }

  public void removeAdapterInstance(String serviceId,
                                    String instanceId) throws IOException {
    var service = getService(serviceId);
    var activeResources = resourceProvider.loadActiveResources();
    var adapter = activeResources.allAdapters()
        .stream()
        .filter(candidate -> serviceId.equals(candidate.getSelectedServiceId()))
        .filter(candidate -> instanceId.equals(candidate.getElementId()))
        .findFirst()
        .map(AdapterDescription::new)
        .orElseGet(() -> makeOrphanedAdapterDescription(instanceId));

    adapter.setRunning(true);

    var requestTarget = ExtensionServiceRequestTargets.adapterStop(service);
    var response = extensionRequestManager.request(
        ExtensionServiceRequests.adapterStateChange(
            requestTarget,
            instanceId,
            serialize(adapter),
            resourceManager
        )
    );

    if (!response.isSuccess()) {
      throw new IOException("Could not remove adapter instance %s from service %s: %s"
          .formatted(instanceId, serviceId, response.responseBody()));
    }
  }

  public void removePipelineElementInstance(String serviceId,
                                            String instanceId) throws IOException {
    var service = getService(serviceId);
    var activeResources = resourceProvider.loadActiveResources();
    var match = activeResources.runningPipelines()
        .stream()
        .flatMap(pipeline -> runningPipelineElements(pipeline)
            .filter(element -> serviceId.equals(element.getSelectedServiceId()))
            .filter(element -> instanceId.equals(InstanceIdExtractor.extractId(element.getElementId())))
            .map(element -> new PipelineElementRemovalTarget(
                element.getAppId(),
                pipeline.getPipelineId(),
                getProvider(element)
            )))
        .filter(Objects::nonNull)
        .findFirst()
        .orElseGet(() -> new PipelineElementRemovalTarget(
            instanceId,
            null,
            SpServiceUrlProvider.DATA_PROCESSOR
        ));

    var requestTarget = ExtensionServiceRequestTargets.pipelineDetach(
        service.getServiceUrl(),
        service.getSvcId(),
        match.provider(),
        match.appId(),
        instanceId
    );
    var response = extensionRequestManager.request(
        ExtensionServiceRequests.pipelineElementDetach(
            requestTarget,
            match.pipelineId(),
            resourceManager
        )
    );

    if (!response.isSuccess()) {
      throw new IOException("Could not remove pipeline element instance %s from service %s: %s"
          .formatted(instanceId, serviceId, response.responseBody()));
    }
  }

  private SpServiceRegistration getService(String serviceId) throws IOException {
    return extensionsServiceStorage.findAll()
        .stream()
        .filter(service -> serviceId.equals(service.getSvcId()))
        .findFirst()
        .orElseThrow(() -> new IOException("Could not find extension service " + serviceId));
  }

  private AdapterDescription makeOrphanedAdapterDescription(String instanceId) {
    var adapterDescription = new AdapterDescription();
    adapterDescription.setElementId(instanceId);
    return adapterDescription;
  }

  private String serialize(AdapterDescription adapterDescription) throws JsonProcessingException {
    return JacksonSerializer.getObjectMapper().writeValueAsString(adapterDescription);
  }

  private Stream<InvocableStreamPipesEntity> runningPipelineElements(Pipeline pipeline) {
    return Stream.concat(
        pipeline.getSepas().stream(),
        pipeline.getActions().stream()
    );
  }

  private SpServiceUrlProvider getProvider(InvocableStreamPipesEntity element) {
    return element instanceof DataSinkInvocation
        ? SpServiceUrlProvider.DATA_SINK
        : SpServiceUrlProvider.DATA_PROCESSOR;
  }

  private record PipelineElementRemovalTarget(String appId,
                                              String pipelineId,
                                              SpServiceUrlProvider provider) {
  }
}
