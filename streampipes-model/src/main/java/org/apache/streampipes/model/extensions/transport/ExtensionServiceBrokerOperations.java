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

package org.apache.streampipes.model.extensions.transport;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ExtensionServiceBrokerOperations {

  public static final ExtensionServiceBrokerOperation CONTAINER_PROVIDED_OPTIONS =
      operation("CONTAINER_PROVIDED_OPTIONS", "container-provided-options");
  public static final ExtensionServiceBrokerOperation MIGRATION =
      operation("MIGRATION", "migration");
  public static final ExtensionServiceBrokerOperation DESCRIPTION_UPDATE =
      operation("DESCRIPTION_UPDATE", "description-update");
  public static final ExtensionServiceBrokerOperation EXTENSION_DESCRIPTION =
      operation("EXTENSION_DESCRIPTION", "extension-description");
  public static final ExtensionServiceBrokerOperation FUNCTION_STOP =
      operation("FUNCTION_STOP", "function-stop");
  public static final ExtensionServiceBrokerOperation ADAPTER_STATE_CHANGE =
      operation("ADAPTER_STATE_CHANGE", "adapter-state-change");
  public static final ExtensionServiceBrokerOperation RUNTIME_OPTIONS =
      operation("RUNTIME_OPTIONS", "adapter-runtime-options");
  public static final ExtensionServiceBrokerOperation SAMPLE_DATA =
      operation("SAMPLE_DATA", "adapter-sample-data");
  public static final ExtensionServiceBrokerOperation EXTENSION_INSTANCE_HEALTH =
      operation("EXTENSION_INSTANCE_HEALTH", "extension-instance-health");
  public static final ExtensionServiceBrokerOperation SERVICE_HEALTH =
      operation("SERVICE_HEALTH", "service-health");
  public static final ExtensionServiceBrokerOperation SERVICE_LOAD =
      operation("SERVICE_LOAD", "monitoring", "service-load");
  public static final ExtensionServiceBrokerOperation PIPELINE_ELEMENT_INVOCATION =
      operation("PIPELINE_ELEMENT_INVOCATION", "pipeline-invocation");
  public static final ExtensionServiceBrokerOperation PIPELINE_ELEMENT_DETACH =
      operation("PIPELINE_ELEMENT_DETACH", "pipeline-detach");
  public static final ExtensionServiceBrokerOperation PIPELINE_ELEMENT_ASSETS =
      operation("PIPELINE_ELEMENT_ASSETS", "pipeline-element-assets");
  public static final ExtensionServiceBrokerOperation PIPELINE_ELEMENT_ICON_ASSET =
      operation("PIPELINE_ELEMENT_ICON_ASSET", "pipeline-element-icon-asset");
  public static final ExtensionServiceBrokerOperation ADAPTER_ASSETS =
      operation("ADAPTER_ASSETS", "adapter-assets");
  public static final ExtensionServiceBrokerOperation ADAPTER_ICON_ASSET =
      operation("ADAPTER_ICON_ASSET", "adapter-icon-asset");
  public static final ExtensionServiceBrokerOperation ADAPTER_DOCUMENTATION_ASSET =
      operation("ADAPTER_DOCUMENTATION_ASSET", "adapter-documentation-asset");
  public static final ExtensionServiceBrokerOperation OUTPUT_SCHEMA =
      operation("OUTPUT_SCHEMA", "output-schema");

  private static final Map<String, ExtensionServiceBrokerOperation> OPERATIONS = Stream.of(
      CONTAINER_PROVIDED_OPTIONS,
      MIGRATION,
      DESCRIPTION_UPDATE,
      EXTENSION_DESCRIPTION,
      FUNCTION_STOP,
      ADAPTER_STATE_CHANGE,
      RUNTIME_OPTIONS,
      SAMPLE_DATA,
      EXTENSION_INSTANCE_HEALTH,
      SERVICE_HEALTH,
      SERVICE_LOAD,
      PIPELINE_ELEMENT_INVOCATION,
      PIPELINE_ELEMENT_DETACH,
      PIPELINE_ELEMENT_ASSETS,
      PIPELINE_ELEMENT_ICON_ASSET,
      ADAPTER_ASSETS,
      ADAPTER_ICON_ASSET,
      ADAPTER_DOCUMENTATION_ASSET,
      OUTPUT_SCHEMA
  ).collect(Collectors.toUnmodifiableMap(ExtensionServiceBrokerOperation::operationId, Function.identity()));

  private ExtensionServiceBrokerOperations() {
  }

  public static ExtensionServiceBrokerOperation byOperationId(String operationId) {
    var operation = OPERATIONS.get(operationId);
    if (operation == null) {
      throw new IllegalArgumentException("Unknown extension broker operation ID: " + operationId);
    }

    return operation;
  }

  private static ExtensionServiceBrokerOperation operation(String operationId, String... topicSegments) {
    return new ExtensionServiceBrokerOperation(operationId, List.of(topicSegments));
  }
}
