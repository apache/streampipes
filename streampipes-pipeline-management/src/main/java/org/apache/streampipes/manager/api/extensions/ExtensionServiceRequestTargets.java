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
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerOperation;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerOperations;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class ExtensionServiceRequestTargets {

  private ExtensionServiceRequestTargets() {
  }

  public static ExtensionServiceRequestTarget containerProvidedOptions(SpServiceRegistration service,
                                                                       SpServiceUrlProvider provider,
                                                                       String appId) {
    return forService(
        service,
        ExtensionServiceBrokerOperations.CONTAINER_PROVIDED_OPTIONS,
        path(provider.getPrefix(), appId, "configurations"),
        topic(ExtensionServiceBrokerOperations.CONTAINER_PROVIDED_OPTIONS, provider.name(), appId)
    );
  }

  public static ExtensionServiceRequestTarget migration(SpServiceRegistration service,
                                                        String type) {
    return forService(
        service,
        ExtensionServiceBrokerOperations.MIGRATION,
        path("api", "v1", "migrations", type),
        topic(ExtensionServiceBrokerOperations.MIGRATION, type)
    );
  }

  public static ExtensionServiceRequestTarget descriptionUpdate(SpServiceRegistration service,
                                                                SpServiceUrlProvider provider,
                                                                String appId) {
    return forService(
        service,
        ExtensionServiceBrokerOperations.DESCRIPTION_UPDATE,
        path(provider.getPrefix(), appId),
        topic(ExtensionServiceBrokerOperations.DESCRIPTION_UPDATE, provider.name(), appId)
    );
  }

  public static ExtensionServiceRequestTarget extensionDescription(SpServiceRegistration service,
                                                                   SpServiceUrlProvider provider,
                                                                   String appId) {
    return forService(
        service,
        ExtensionServiceBrokerOperations.EXTENSION_DESCRIPTION,
        path(provider.getPrefix(), appId),
        topic(ExtensionServiceBrokerOperations.EXTENSION_DESCRIPTION, provider.name(), appId)
    );
  }

  public static ExtensionServiceRequestTarget functionStop(SpServiceRegistration service) {
    return forService(
        service,
        ExtensionServiceBrokerOperations.FUNCTION_STOP,
        path("api", "v1", "functions", "stop"),
        topic(ExtensionServiceBrokerOperations.FUNCTION_STOP)
    );
  }

  public static ExtensionServiceRequestTarget adapterStart(SpServiceRegistration service) {
    return forService(
        service,
        ExtensionServiceBrokerOperations.ADAPTER_STATE_CHANGE,
        path("api", "v1", "worker", "stream", "invoke"),
        topic(ExtensionServiceBrokerOperations.ADAPTER_STATE_CHANGE, "start")
    );
  }

  public static ExtensionServiceRequestTarget adapterStop(SpServiceRegistration service) {
    return forService(
        service,
        ExtensionServiceBrokerOperations.ADAPTER_STATE_CHANGE,
        path("api", "v1", "worker", "stream", "stop"),
        topic(ExtensionServiceBrokerOperations.ADAPTER_STATE_CHANGE, "stop")
    );
  }

  public static ExtensionServiceRequestTarget adapterRuntimeOptions(SpServiceRegistration service,
                                                                    String appId) {
    return forService(service, ExtensionServiceBrokerOperations.RUNTIME_OPTIONS,
        path("api", "v1", "worker", "resolvable", appId, "configurations"),
        topic(ExtensionServiceBrokerOperations.RUNTIME_OPTIONS, appId));
  }

  public static ExtensionServiceRequestTarget adapterSampleData(SpServiceRegistration service) {
    return forService(
        service,
        ExtensionServiceBrokerOperations.SAMPLE_DATA,
        path("api", "v1", "worker", "guess", "sample"),
        topic(ExtensionServiceBrokerOperations.SAMPLE_DATA)
    );
  }

  public static ExtensionServiceRequestTarget extensionInstanceHealth(SpServiceRegistration service) {
    return forService(
        service,
        ExtensionServiceBrokerOperations.EXTENSION_INSTANCE_HEALTH,
        path("health"),
        topic(ExtensionServiceBrokerOperations.EXTENSION_INSTANCE_HEALTH)
    );
  }

  public static ExtensionServiceRequestTarget serviceHealth(SpServiceRegistration service,
                                                            String healthCheckPath) {
    return forService(
        service,
        ExtensionServiceBrokerOperations.SERVICE_HEALTH,
        path(healthCheckPath),
        topic(ExtensionServiceBrokerOperations.SERVICE_HEALTH)
    );
  }

  public static ExtensionServiceRequestTarget serviceLoad(SpServiceRegistration service) {
    return forService(
        service,
        ExtensionServiceBrokerOperations.SERVICE_LOAD,
        path("serviceMonitor"),
        topic(ExtensionServiceBrokerOperations.SERVICE_LOAD)
    );
  }

  public static ExtensionServiceRequestTarget pipelineInvocation(String baseUrl,
                                                                 String serviceId,
                                                                 SpServiceUrlProvider provider,
                                                                 String appId) {
    return ExtensionServiceRequestTarget.of(baseUrl, serviceId, ExtensionServiceBrokerOperations.PIPELINE_ELEMENT_INVOCATION.operationId(),
        path(provider.getPrefix(), appId),
        topic(ExtensionServiceBrokerOperations.PIPELINE_ELEMENT_INVOCATION, provider.name(), appId));
  }

  public static ExtensionServiceRequestTarget pipelineDetach(String baseUrl,
                                                             String serviceId,
                                                             SpServiceUrlProvider provider,
                                                             String appId,
                                                             String instanceId) {
    return ExtensionServiceRequestTarget.of(baseUrl, serviceId, ExtensionServiceBrokerOperations.PIPELINE_ELEMENT_DETACH.operationId(),
        path(provider.getPrefix(), appId, instanceId),
        topic(ExtensionServiceBrokerOperations.PIPELINE_ELEMENT_DETACH, provider.name(), appId, instanceId));
  }

  public static ExtensionServiceRequestTarget pipelineElementAssets(SpServiceRegistration service,
                                                                    SpServiceUrlProvider provider,
                                                                    String appId) {
    return forService(
        service,
        ExtensionServiceBrokerOperations.PIPELINE_ELEMENT_ASSETS,
        path(provider.getPrefix(), appId, "assets"),
        topic(ExtensionServiceBrokerOperations.PIPELINE_ELEMENT_ASSETS, provider.name(), appId)
    );
  }

  public static ExtensionServiceRequestTarget adapterAssets(SpServiceRegistration service,
                                                            String appId) {
    return forService(service, ExtensionServiceBrokerOperations.ADAPTER_ASSETS,
        path("api", "v1", "worker", "adapters", appId, "assets"),
        topic(ExtensionServiceBrokerOperations.ADAPTER_ASSETS, appId));
  }

  public static ExtensionServiceRequestTarget adapterIconAsset(SpServiceRegistration service,
                                                               String appId) {
    return forService(service, ExtensionServiceBrokerOperations.ADAPTER_ICON_ASSET,
        path("api", "v1", "worker", "adapters", appId, "assets", "icon"),
        topic(ExtensionServiceBrokerOperations.ADAPTER_ICON_ASSET, appId));
  }

  public static ExtensionServiceRequestTarget adapterDocumentationAsset(SpServiceRegistration service,
                                                                        String appId) {
    return forService(service, ExtensionServiceBrokerOperations.ADAPTER_DOCUMENTATION_ASSET,
        path("api", "v1", "worker", "adapters", appId, "assets", "documentation"),
        topic(ExtensionServiceBrokerOperations.ADAPTER_DOCUMENTATION_ASSET, appId));
  }

  public static ExtensionServiceRequestTarget outputSchema(SpServiceRegistration service,
                                                           SpServiceUrlProvider provider,
                                                           String appId) {
    return forService(
        service,
        ExtensionServiceBrokerOperations.OUTPUT_SCHEMA,
        path(provider.getPrefix(), appId, "output"),
        topic(ExtensionServiceBrokerOperations.OUTPUT_SCHEMA, provider.name(), appId)
    );
  }

  private static ExtensionServiceRequestTarget forService(SpServiceRegistration service,
                                                          ExtensionServiceBrokerOperation operation,
                                                          List<String> pathSegments,
                                                          List<String> topicSegments) {
    return ExtensionServiceRequestTarget.of(
        service.getServiceUrl(),
        service.getSvcId(),
        operation.operationId(),
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

  private static List<String> topic(ExtensionServiceBrokerOperation operation, String... dynamicTopicSegments) {
    return Stream.concat(
            operation.topicPrefixSegments().stream(),
            Arrays.stream(dynamicTopicSegments)
        )
        .filter(segment -> segment != null && !segment.isBlank())
        .toList();
  }
}
