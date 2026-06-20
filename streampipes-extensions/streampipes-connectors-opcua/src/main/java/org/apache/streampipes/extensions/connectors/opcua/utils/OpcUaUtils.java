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

package org.apache.streampipes.extensions.connectors.opcua.utils;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOptions;
import org.apache.streampipes.extensions.connectors.opcua.adapter.OpcUaNodeBrowser;
import org.apache.streampipes.extensions.connectors.opcua.alarms.OpcUaEventFieldProvider;
import org.apache.streampipes.extensions.connectors.opcua.alarms.OpcUaEventTypeBrowser;
import org.apache.streampipes.extensions.connectors.opcua.alarms.OpcUaNotifierBrowser;
import org.apache.streampipes.extensions.connectors.opcua.client.OpcUaClientProvider;
import org.apache.streampipes.extensions.connectors.opcua.config.OpcUaAdapterConfig;
import org.apache.streampipes.extensions.connectors.opcua.config.OpcUaConfig;
import org.apache.streampipes.extensions.connectors.opcua.config.SharedUserConfiguration;
import org.apache.streampipes.extensions.connectors.opcua.config.SpOpcUaConfigExtractor;
import org.apache.streampipes.extensions.management.client.StreamPipesClientResolver;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableTreeInputStaticProperty;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/***
 * Collection of several utility functions in context of OPC UA
 */
public class OpcUaUtils {

  private static final String OPC_TCP_PREFIX = "opc.tcp://";

  /***
   * Ensures server address starts with {@code opc.tcp://}
   * @param serverAddress server address as given by user
   * @return correctly formated server address
   */
  public static String addOpcPrefixIfNotExists(String serverAddress) {
    return serverAddress.startsWith(OPC_TCP_PREFIX) ? serverAddress : OPC_TCP_PREFIX + serverAddress;
  }

  /***
   * OPC UA specific implementation of
   * {@link ResolvesContainerProvidedOptions resolveOptions(String, StaticPropertyExtractor)}.
   * @param internalName The internal name of the Static Property
   * @param parameterExtractor to extract parameters from the OPC UA config
   * @return {@code List<Option>} with available node names for the given OPC UA configuration
   */
  public static RuntimeResolvableTreeInputStaticProperty resolveConfig(OpcUaClientProvider clientProvider,
                                                                       String internalName,
                                                                       IStaticPropertyExtractor parameterExtractor)
      throws SpConfigurationException {

    var client = new StreamPipesClientResolver().makeStreamPipesClientInstance();
    RuntimeResolvableTreeInputStaticProperty config = parameterExtractor
        .getStaticPropertyByName(internalName, RuntimeResolvableTreeInputStaticProperty.class);
    // access mode and host/url have to be selected
    try {
      parameterExtractor.selectedAlternativeInternalId(OpcUaLabels.OPC_HOST_OR_URL.name());
      parameterExtractor.selectedSingleValueInternalName(SharedUserConfiguration.SECURITY_MODE, String.class);
      parameterExtractor.selectedSingleValue(SharedUserConfiguration.SECURITY_POLICY, String.class);
    } catch (NullPointerException nullPointerException) {
      return config;
    }

    var opcUaConfig = SpOpcUaConfigExtractor.extractSharedConfig(parameterExtractor, new OpcUaAdapterConfig(), client);
    try {
      var connectedClient = clientProvider.getClient(opcUaConfig);
      OpcUaNodeBrowser nodeBrowser =
          new OpcUaNodeBrowser(connectedClient.getClient(), opcUaConfig);

      var nodes = nodeBrowser.buildNodeTreeFromOrigin(config.getNextBaseNodeToResolve());
      if (Objects.isNull(config.getNextBaseNodeToResolve())) {
        config.setNodes(nodes);
      } else {
        config.setLatestFetchedNodes(nodes);
      }

      if (!config.getSelectedNodesInternalNames().isEmpty()) {
        config.setSelectedNodesInternalNames(
            filterMissingNodes(connectedClient.getClient(), config.getSelectedNodesInternalNames())
        );
      }

      return config;
    } catch (UaException e) {
      if (OpcUaCertificateUtils.isCertificateException(e)) {
        throw new SpConfigurationException(
            OpcUaCertificateUtils.makeExceptionMessage(e, opcUaConfig)
        );
      }
      throw new SpConfigurationException(ExceptionMessageExtractor.getDescription(e));
    } catch (ExecutionException | InterruptedException | URISyntaxException e) {
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      throw makeConnectionException(opcUaConfig, e);
    } finally {
      clientProvider.releaseClient(opcUaConfig);
    }
  }

  public static List<String> filterMissingNodes(OpcUaClient opcUaClient,
                                                List<String> selectedNodes) {
    return selectedNodes.stream().filter(selectedNode -> {
      try {
        var node = opcUaClient.getAddressSpace().getNode(NodeId.parse(selectedNode));
        var value = node.readAttribute(AttributeId.Value);
        var statusCode = value.getStatusCode();
        return statusCode != null && statusCode.isGood();
      } catch (UaException e) {
        return false;
      }
    }).toList();
  }

  public static RuntimeResolvableTreeInputStaticProperty resolveNotifierTreeConfig(
      OpcUaClientProvider clientProvider,
      String internalName,
      IStaticPropertyExtractor parameterExtractor
  )
      throws SpConfigurationException {
    var client = new StreamPipesClientResolver().makeStreamPipesClientInstance();
    var config = parameterExtractor.getStaticPropertyByName(
        internalName,
        RuntimeResolvableTreeInputStaticProperty.class
    );

    try {
      parameterExtractor.selectedAlternativeInternalId(OpcUaLabels.OPC_HOST_OR_URL.name());
      parameterExtractor.selectedSingleValueInternalName(SharedUserConfiguration.SECURITY_MODE, String.class);
      parameterExtractor.selectedSingleValue(SharedUserConfiguration.SECURITY_POLICY, String.class);
    } catch (NullPointerException nullPointerException) {
      return config;
    }

    var opcUaConfig = SpOpcUaConfigExtractor.extractConnectionConfig(parameterExtractor, new OpcUaConfig(), client);

    try {
      var connectedClient = clientProvider.getClient(opcUaConfig);
      var nodeBrowser = new OpcUaNotifierBrowser(connectedClient.getClient());
      var nodes = nodeBrowser.buildNodeTreeFromOrigin(config.getNextBaseNodeToResolve());
      if (Objects.isNull(config.getNextBaseNodeToResolve())) {
        config.setNodes(nodes);
      } else {
        config.setLatestFetchedNodes(nodes);
      }

      if (!config.getSelectedNodesInternalNames().isEmpty()) {
        config.setSelectedNodesInternalNames(
            filterExistingNodes(connectedClient.getClient(), config.getSelectedNodesInternalNames(), config.isMultiSelection())
        );
      }

      return config;
    } catch (UaException e) {
      if (OpcUaCertificateUtils.isCertificateException(e)) {
        throw new SpConfigurationException(
            OpcUaCertificateUtils.makeExceptionMessage(e, opcUaConfig)
        );
      }
      throw new SpConfigurationException(ExceptionMessageExtractor.getDescription(e));
    } catch (ExecutionException | InterruptedException | URISyntaxException e) {
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      throw makeConnectionException(opcUaConfig, e);
    } finally {
      clientProvider.releaseClient(opcUaConfig);
    }
  }

  public static RuntimeResolvableTreeInputStaticProperty resolveEventTypeTreeConfig(
      OpcUaClientProvider clientProvider,
      String internalName,
      IStaticPropertyExtractor parameterExtractor
  ) throws SpConfigurationException {
    var client = new StreamPipesClientResolver().makeStreamPipesClientInstance();
    var config = parameterExtractor.getStaticPropertyByName(
        internalName,
        RuntimeResolvableTreeInputStaticProperty.class
    );

    try {
      parameterExtractor.selectedAlternativeInternalId(OpcUaLabels.OPC_HOST_OR_URL.name());
      parameterExtractor.selectedSingleValueInternalName(SharedUserConfiguration.SECURITY_MODE, String.class);
      parameterExtractor.selectedSingleValue(SharedUserConfiguration.SECURITY_POLICY, String.class);
    } catch (NullPointerException nullPointerException) {
      return config;
    }

    var opcUaConfig = SpOpcUaConfigExtractor.extractConnectionConfig(parameterExtractor, new OpcUaConfig(), client);

    try {
      var connectedClient = clientProvider.getClient(opcUaConfig);
      var typeBrowser = new OpcUaEventTypeBrowser(connectedClient.getClient());
      var nodes = typeBrowser.buildNodeTreeFromOrigin(config.getNextBaseNodeToResolve());
      if (Objects.isNull(config.getNextBaseNodeToResolve())) {
        config.setNodes(nodes);
      } else {
        config.setLatestFetchedNodes(nodes);
      }

      if (!config.getSelectedNodesInternalNames().isEmpty()) {
        config.setSelectedNodesInternalNames(
            filterExistingNodes(connectedClient.getClient(), config.getSelectedNodesInternalNames(), config.isMultiSelection())
        );
      }

      return config;
    } catch (UaException e) {
      if (OpcUaCertificateUtils.isCertificateException(e)) {
        throw new SpConfigurationException(
            OpcUaCertificateUtils.makeExceptionMessage(e, opcUaConfig)
        );
      }
      throw new SpConfigurationException(ExceptionMessageExtractor.getDescription(e));
    } catch (ExecutionException | InterruptedException | URISyntaxException e) {
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      throw makeConnectionException(opcUaConfig, e);
    } finally {
      clientProvider.releaseClient(opcUaConfig);
    }
  }

  public static RuntimeResolvableAnyStaticProperty resolveEventFieldConfig(
      OpcUaClientProvider clientProvider,
      String internalName,
      IStaticPropertyExtractor parameterExtractor
  ) throws SpConfigurationException {
    var client = new StreamPipesClientResolver().makeStreamPipesClientInstance();
    var config = parameterExtractor.getStaticPropertyByName(
        internalName,
        RuntimeResolvableAnyStaticProperty.class
    );

    try {
      parameterExtractor.selectedAlternativeInternalId(OpcUaLabels.OPC_HOST_OR_URL.name());
      parameterExtractor.selectedSingleValueInternalName(SharedUserConfiguration.SECURITY_MODE, String.class);
      parameterExtractor.selectedSingleValue(SharedUserConfiguration.SECURITY_POLICY, String.class);
    } catch (NullPointerException nullPointerException) {
      return config;
    }

    var selectedEventTypes = parameterExtractor.selectedTreeNodesInternalNames(
        org.apache.streampipes.extensions.connectors.opcua.alarms.OpcUaAlarmConfiguration.EVENT_TYPE,
        String.class
    );
    if (selectedEventTypes.isEmpty()) {
      config.setOptions(List.of());
      return config;
    }

    var opcUaConfig = SpOpcUaConfigExtractor.extractConnectionConfig(parameterExtractor, new OpcUaConfig(), client);

    try {
      var connectedClient = clientProvider.getClient(opcUaConfig);
      var fieldProvider = new OpcUaEventFieldProvider(connectedClient.getClient());
      config.setOptions(fieldProvider.buildAdditionalFieldOptions(selectedEventTypes.get(0), config.getOptions()));
      return config;
    } catch (UaException e) {
      if (OpcUaCertificateUtils.isCertificateException(e)) {
        throw new SpConfigurationException(
            OpcUaCertificateUtils.makeExceptionMessage(e, opcUaConfig)
        );
      }
      throw new SpConfigurationException(ExceptionMessageExtractor.getDescription(e));
    } catch (ExecutionException | InterruptedException | URISyntaxException e) {
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      throw makeConnectionException(opcUaConfig, e);
    } finally {
      clientProvider.releaseClient(opcUaConfig);
    }
  }

  private static SpConfigurationException makeConnectionException(OpcUaConfig opcUaConfig,
                                                                  Throwable throwable) {
    String detail = ExceptionMessageExtractor.getDescription(throwable);
    String serverUrl = opcUaConfig.getOpcServerURL();

    if (detail == null || detail.isBlank()) {
      return new SpConfigurationException(
          "Could not connect to the OPC UA server at " + serverUrl + "."
      );
    }

    return new SpConfigurationException(
        "Could not connect to the OPC UA server at " + serverUrl + ": " + detail
    );
  }

  private static List<String> filterExistingNodes(OpcUaClient opcUaClient,
                                                  List<String> selectedNodes,
                                                  boolean multiSelection) {
    var filtered = selectedNodes.stream()
        .filter(selectedNode -> {
          try {
            opcUaClient.getAddressSpace().getNode(NodeId.parse(selectedNode));
            return true;
          } catch (UaException e) {
            return false;
          }
        })
        .toList();

    if (!multiSelection && !filtered.isEmpty()) {
      return List.of(filtered.get(0));
    }

    return filtered;
  }
}
