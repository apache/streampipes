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
import org.apache.streampipes.extensions.connectors.opcua.client.OpcUaClientProvider;
import org.apache.streampipes.extensions.connectors.opcua.config.OpcUaAdapterConfig;
import org.apache.streampipes.extensions.connectors.opcua.config.SharedUserConfiguration;
import org.apache.streampipes.extensions.connectors.opcua.config.SpOpcUaConfigExtractor;
import org.apache.streampipes.extensions.connectors.opcua.config.security.CompositeCertificateValidator;
import org.apache.streampipes.extensions.management.client.StreamPipesClientResolver;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableTreeInputStaticProperty;

import org.eclipse.milo.opcua.sdk.client.api.UaClient;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/***
 * Collection of several utility functions in context of OPC UA
 */
public class OpcUaUtils {

  private static final Logger LOG = LoggerFactory.getLogger(OpcUaUtils.class);

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
        throw new SpConfigurationException(ExceptionMessageExtractor.getDescription(e), e);
    } catch (ExecutionException | InterruptedException | URISyntaxException e) {
      if (e instanceof ExecutionException && isCertificateException((ExecutionException) e)) {
        throw new SpConfigurationException(
            makeExceptionMessage((ExecutionException) e)
        );
      } else {
        throw new SpConfigurationException("Could not connect to the OPC UA server with the provided settings", e);
      }
    } finally {
      clientProvider.releaseClient(opcUaConfig);
    }
  }

  public static List<String> filterMissingNodes(UaClient opcUaClient,
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

  public static String getCoreCertificatePath() {
    return "/api/v2/admin/certificates";
  }

  public static String getCoreTrustedCertificatePath() {
    return getCoreCertificatePath() + "/trusted";
  }

  private static boolean isCertificateException(ExecutionException e) {
    Throwable cause = e.getCause();

    if (cause instanceof UaException uaException) {
      return checkAndLogCertificateException(uaException);
    }

    Throwable nestedCause = cause != null ? cause.getCause() : null;
    if (nestedCause instanceof UaException uaException) {
      return checkAndLogCertificateException(uaException);
    }

    return false;
  }

  private static boolean checkAndLogCertificateException(UaException e) {
    var containsRejectedStatusCode = CompositeCertificateValidator.REJECTED_STATUS_CODES
        .contains(e.getStatusCode().getValue());

    if (containsRejectedStatusCode) {
      var statusCode = CompositeCertificateValidator.REJECTED_STATUS_CODES.stream().filter(code -> code.equals(e.getStatusCode().getValue())).findFirst();
      statusCode.ifPresent(sc -> LOG.warn("Status Code: {}", sc));
    }
    return containsRejectedStatusCode;
  }

  private static String makeExceptionMessage(ExecutionException e) {
    StringBuilder message = new StringBuilder(
       "The provided certificate could not be trusted. Administrators can accept this certificate in the settings. "
    );
    Throwable cause = e.getCause();
    if (cause != null) {
      message.append("Reason: ").append(cause.getMessage());
    }
    return message.toString();
  }
}
