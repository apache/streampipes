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

package org.apache.streampipes.manager.api.extensions;

import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import java.util.Arrays;
import java.util.List;

public final class ExtensionServiceRequestTargets {

  private ExtensionServiceRequestTargets() {
  }

  public static ExtensionServiceRequestTarget containerProvidedOptions(SpServiceRegistration service,
                                                                       SpServiceUrlProvider provider,
                                                                       String appId) {
    return forService(
        service,
        ExtensionServiceOperationType.CONTAINER_PROVIDED_OPTIONS,
        path(provider.getPrefix(), appId, "configurations"),
        topic("container-provided-options", provider.name(), appId)
    );
  }

  public static ExtensionServiceRequestTarget migration(SpServiceRegistration service,
                                                        String type) {
    return forService(
        service,
        ExtensionServiceOperationType.MIGRATION,
        path("api", "v1", "migrations", type),
        topic("migration", type)
    );
  }

  public static ExtensionServiceRequestTarget descriptionUpdate(SpServiceRegistration service,
                                                                SpServiceUrlProvider provider,
                                                                String appId) {
    return forService(
        service,
        ExtensionServiceOperationType.DESCRIPTION_UPDATE,
        path(provider.getPrefix(), appId),
        topic("description-update", provider.name(), appId)
    );
  }

  public static ExtensionServiceRequestTarget extensionDescription(SpServiceRegistration service,
                                                                   SpServiceUrlProvider provider,
                                                                   String appId) {
    return forService(
        service,
        ExtensionServiceOperationType.EXTENSION_DESCRIPTION,
        path(provider.getPrefix(), appId),
        topic("extension-description", provider.name(), appId)
    );
  }

  public static ExtensionServiceRequestTarget functionStop(SpServiceRegistration service) {
    return forService(
        service,
        ExtensionServiceOperationType.FUNCTION_STOP,
        path("api", "v1", "functions", "stop"),
        topic("function-stop")
    );
  }

  public static ExtensionServiceRequestTarget adapterStart(SpServiceRegistration service) {
    return forService(
        service,
        ExtensionServiceOperationType.ADAPTER_STATE_CHANGE,
        path("api", "v1", "worker", "stream", "invoke"),
        topic("adapter-state-change", "start")
    );
  }

  public static ExtensionServiceRequestTarget adapterStop(SpServiceRegistration service) {
    return forService(
        service,
        ExtensionServiceOperationType.ADAPTER_STATE_CHANGE,
        path("api", "v1", "worker", "stream", "stop"),
        topic("adapter-state-change", "stop")
    );
  }

  public static ExtensionServiceRequestTarget adapterRuntimeOptions(SpServiceRegistration service,
                                                                    String appId) {
    return forService(service, ExtensionServiceOperationType.RUNTIME_OPTIONS,
        path("api", "v1", "worker", "resolvable", appId, "configurations"),
        topic("adapter-runtime-options", appId));
  }

  public static ExtensionServiceRequestTarget adapterSampleData(SpServiceRegistration service) {
    return forService(
        service,
        ExtensionServiceOperationType.SAMPLE_DATA,
        path("api", "v1", "worker", "guess", "sample"),
        topic("adapter-sample-data")
    );
  }

  public static ExtensionServiceRequestTarget extensionInstanceHealth(SpServiceRegistration service) {
    return forService(
        service,
        ExtensionServiceOperationType.EXTENSION_INSTANCE_HEALTH,
        path("health"),
        topic("extension-instance-health")
    );
  }

  public static ExtensionServiceRequestTarget serviceHealth(SpServiceRegistration service,
                                                            String healthCheckPath) {
    return forService(
        service,
        ExtensionServiceOperationType.SERVICE_HEALTH,
        path(healthCheckPath),
        topic("service-health")
    );
  }

  public static ExtensionServiceRequestTarget pipelineInvocation(String baseUrl,
                                                                 String serviceId,
                                                                 SpServiceUrlProvider provider,
                                                                 String appId) {
    return ExtensionServiceRequestTarget.of(baseUrl, serviceId, ExtensionServiceOperationType.PIPELINE_ELEMENT_INVOCATION,
        path(provider.getPrefix(), appId),
        topic("pipeline-invocation", provider.name(), appId));
  }

  public static ExtensionServiceRequestTarget pipelineDetach(String baseUrl,
                                                             String serviceId,
                                                             SpServiceUrlProvider provider,
                                                             String appId,
                                                             String instanceId) {
    return ExtensionServiceRequestTarget.of(baseUrl, serviceId, ExtensionServiceOperationType.PIPELINE_ELEMENT_DETACH,
        path(provider.getPrefix(), appId, instanceId),
        topic("pipeline-detach", provider.name(), appId, instanceId));
  }

  public static ExtensionServiceRequestTarget pipelineElementAssets(SpServiceRegistration service,
                                                                    SpServiceUrlProvider provider,
                                                                    String appId) {
    return forService(
        service,
        ExtensionServiceOperationType.PIPELINE_ELEMENT_ASSETS,
        path(provider.getPrefix(), appId, "assets"),
        topic("pipeline-element-assets", provider.name(), appId)
    );
  }

  public static ExtensionServiceRequestTarget adapterAssets(SpServiceRegistration service,
                                                            String appId) {
    return forService(service, ExtensionServiceOperationType.ADAPTER_ASSETS,
        path("api", "v1", "worker", "adapters", appId, "assets"),
        topic("adapter-assets", appId));
  }

  public static ExtensionServiceRequestTarget adapterIconAsset(SpServiceRegistration service,
                                                               String appId) {
    return forService(service, ExtensionServiceOperationType.ADAPTER_ICON_ASSET,
        path("api", "v1", "worker", "adapters", appId, "assets", "icon"),
        topic("adapter-icon-asset", appId));
  }

  public static ExtensionServiceRequestTarget adapterDocumentationAsset(SpServiceRegistration service,
                                                                        String appId) {
    return forService(service, ExtensionServiceOperationType.ADAPTER_DOCUMENTATION_ASSET,
        path("api", "v1", "worker", "adapters", appId, "assets", "documentation"),
        topic("adapter-documentation-asset", appId));
  }

  public static ExtensionServiceRequestTarget outputSchema(SpServiceRegistration service,
                                                           SpServiceUrlProvider provider,
                                                           String appId) {
    return forService(
        service,
        ExtensionServiceOperationType.OUTPUT_SCHEMA,
        path(provider.getPrefix(), appId, "output"),
        topic("output-schema", provider.name(), appId)
    );
  }

  private static ExtensionServiceRequestTarget forService(SpServiceRegistration service,
                                                          ExtensionServiceOperationType operation,
                                                          List<String> pathSegments,
                                                          List<String> topicSegments) {
    return ExtensionServiceRequestTarget.of(
        service.getServiceUrl(),
        service.getSvcId(),
        operation,
        pathSegments,
        topicSegments
    );
  }

  private static String[] splitPath(String path) {
    if (path == null) {
      return new String[0];
    }

    return Arrays.stream(path.split("/"))
        .filter(part -> !part.isBlank())
        .toArray(String[]::new);
  }

  private static List<String> path(String... pathSegments) {
    return Arrays.asList(pathSegments);
  }

  private static List<String> topic(String... topicSegments) {
    return Arrays.stream(topicSegments)
        .filter(segment -> segment != null && !segment.isBlank())
        .toList();
  }
}
