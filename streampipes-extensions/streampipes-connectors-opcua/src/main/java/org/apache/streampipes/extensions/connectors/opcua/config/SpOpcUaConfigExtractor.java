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

package org.apache.streampipes.extensions.connectors.opcua.config;

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.extensions.api.extractor.IParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.connectors.opcua.alarms.OpcUaAlarmAdapterConfig;
import org.apache.streampipes.extensions.connectors.opcua.alarms.OpcUaAlarmConfiguration;
import org.apache.streampipes.extensions.connectors.opcua.config.identity.AnonymousIdentityConfig;
import org.apache.streampipes.extensions.connectors.opcua.config.identity.UsernamePasswordIdentityConfig;
import org.apache.streampipes.extensions.connectors.opcua.config.identity.X509IdentityConfig;
import org.apache.streampipes.extensions.connectors.opcua.config.security.SecurityConfig;
import org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels;
import org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaNamingStrategy;
import org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaUtils;

import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;

import java.util.Collections;
import java.util.List;

import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.ADAPTER_TYPE;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.AVAILABLE_NODES;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_HOST_OR_URL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_SERVER_HOST;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_SERVER_PORT;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_SERVER_URL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_URL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.PASSWORD;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.PULLING_INTERVAL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.PULL_MODE;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.SUBSCRIPTION_DISCARD_OLDEST;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.SUBSCRIPTION_PUBLISHING_INTERVAL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.SUBSCRIPTION_QUEUE_SIZE;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.SUBSCRIPTION_SAMPLING_INTERVAL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.USERNAME;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.USERNAME_GROUP;

public class SpOpcUaConfigExtractor {


  public static OpcUaAdapterConfig extractAdapterConfig(IStaticPropertyExtractor extractor,
                                                        IStreamPipesClient streamPipesClient,
                                                        String adapterId) {
    var config = extractAdapterConfig(extractor, streamPipesClient);
    config.setAssociatedResourceId(adapterId);
    return config;
  }

  /**
   * Creates {@link OpcUaAdapterConfig}  instance in accordance with the given
   * {@link org.apache.streampipes.sdk.extractor.StaticPropertyExtractor}.
   *
   * @param extractor extractor for user inputs
   * @return {@link OpcUaAdapterConfig}  instance based on information from {@code extractor}
   */
  public static OpcUaAdapterConfig extractAdapterConfig(IStaticPropertyExtractor extractor,
                                                        IStreamPipesClient streamPipesClient) {
    var config = extractSharedConfig(extractor, new OpcUaAdapterConfig(), streamPipesClient);
    boolean usePullMode = extractor.selectedAlternativeInternalId(ADAPTER_TYPE.name())
        .equals(PULL_MODE.name());

    if (usePullMode) {
      Integer pullIntervalSeconds =
          extractor.singleValueParameter(PULLING_INTERVAL.name(), Integer.class);
      var incompleteEventStrategy = extractor.selectedSingleValueInternalName(
          SharedUserConfiguration.INCOMPLETE_EVENT_HANDLING_KEY, String.class
      );

      config.setPullIntervalMilliSeconds(pullIntervalSeconds);
      config.setIncompleteEventStrategy(incompleteEventStrategy);
    } else {
      config.setSubscriptionPublishingIntervalMs(extractOptionalInteger(
          extractor,
          SUBSCRIPTION_PUBLISHING_INTERVAL.name(),
          OpcUaAdapterConfig.DEFAULT_SUBSCRIPTION_PUBLISHING_INTERVAL_MS
      ));
      config.setSubscriptionSamplingIntervalMs(extractOptionalInteger(
          extractor,
          SUBSCRIPTION_SAMPLING_INTERVAL.name(),
          OpcUaAdapterConfig.DEFAULT_SUBSCRIPTION_SAMPLING_INTERVAL_MS
      ));
      config.setSubscriptionQueueSize(extractOptionalInteger(
          extractor,
          SUBSCRIPTION_QUEUE_SIZE.name(),
          OpcUaAdapterConfig.DEFAULT_SUBSCRIPTION_QUEUE_SIZE
      ));
      var discardOldest = extractOptionalSelectedInternalName(
          extractor,
          SUBSCRIPTION_DISCARD_OLDEST.name(),
          String.valueOf(OpcUaAdapterConfig.DEFAULT_SUBSCRIPTION_DISCARD_OLDEST)
      );
      config.setSubscriptionDiscardOldest(Boolean.parseBoolean(discardOldest));
      var incompleteEventStrategy = extractOptionalSelectedInternalName(
          extractor,
          SharedUserConfiguration.INCOMPLETE_EVENT_HANDLING_KEY,
          SharedUserConfiguration.INCOMPLETE_OPTION_IGNORE
      );
      config.setIncompleteEventStrategy(incompleteEventStrategy);
    }

    var namingStrategySelection = extractor.selectedSingleValueInternalName(
        OpcUaLabels.NAMING_STRATEGY.name(), String.class
    );
    var namingStrategy = OpcUaNamingStrategy.valueOf(namingStrategySelection);
    config.setNamingStrategy(namingStrategy);

    return config;
  }

  private static Integer extractOptionalInteger(IStaticPropertyExtractor extractor,
                                                String internalName,
                                                Integer defaultValue) {
    try {
      var value = extractor.singleValueParameter(internalName, Integer.class);
      return value != null ? value : defaultValue;
    } catch (RuntimeException e) {
      return defaultValue;
    }
  }

  private static String extractOptionalSelectedInternalName(IStaticPropertyExtractor extractor,
                                                           String internalName,
                                                           String defaultValue) {
    try {
      var value = extractor.selectedSingleValueInternalName(internalName, String.class);
      return value != null ? value : defaultValue;
    } catch (RuntimeException e) {
      return defaultValue;
    }
  }

  public static OpcUaConfig extractSinkConfig(IParameterExtractor extractor,
                                              IStreamPipesClient streamPipesClient) {
    return extractSharedConfig(extractor, new OpcUaConfig(), streamPipesClient);
  }

  public static OpcUaAlarmAdapterConfig extractAlarmAdapterConfig(IStaticPropertyExtractor extractor,
                                                                  IStreamPipesClient streamPipesClient,
                                                                  String adapterId) {
    var config = extractConnectionConfig(extractor, new OpcUaAlarmAdapterConfig(), streamPipesClient);
    config.setAssociatedResourceId(adapterId);

    var alarmSourceScope = extractor.selectedAlternativeInternalId(OpcUaAlarmConfiguration.ALARM_SOURCE_SCOPE);
    if (OpcUaAlarmConfiguration.SPECIFIC_AREA_OR_MACHINE.equals(alarmSourceScope)) {
      var selectedNotifierNodes =
          extractor.selectedTreeNodesInternalNames(OpcUaAlarmConfiguration.NOTIFIER_NODE, String.class);
      if (!selectedNotifierNodes.isEmpty()) {
        config.setNotifierNodeId(selectedNotifierNodes.get(0));
      }
    }
    var selectedEventTypes =
        extractor.selectedTreeNodesInternalNames(OpcUaAlarmConfiguration.EVENT_TYPE, String.class);
    if (!selectedEventTypes.isEmpty()) {
      config.setEventTypeNodeId(selectedEventTypes.get(0));
    }
    config.setSelectedAdditionalFieldNames(
        extractor.selectedMultiValuesInternalNames(OpcUaAlarmConfiguration.ADDITIONAL_FIELDS, String.class)
    );

    var alarmFilterMode = extractor.selectedAlternativeInternalId(OpcUaAlarmConfiguration.ALARM_FILTER_MODE);
    if (OpcUaAlarmConfiguration.SOURCE_NAME_CONTAINS.equals(alarmFilterMode)) {
      config.setSourceNameFilter(extractor.textParameter(OpcUaAlarmConfiguration.SOURCE_NAME_FILTER));
    }

    var minimumSeverity = extractor.selectedSingleValueInternalName(OpcUaAlarmConfiguration.MINIMUM_SEVERITY, String.class);
    if (!OpcUaAlarmConfiguration.MINIMUM_SEVERITY_ANY.equals(minimumSeverity)) {
      config.setMinimumSeverity(Integer.parseInt(minimumSeverity));
    }

    return config;
  }

  public static <T extends OpcUaConfig> T extractSharedConfig(IParameterExtractor extractor,
                                                              T config,
                                                              IStreamPipesClient streamPipesClient) {
    return extractSharedConfig(extractor, config, streamPipesClient, true);
  }

  public static <T extends OpcUaConfig> T extractConnectionConfig(IParameterExtractor extractor,
                                                                  T config,
                                                                  IStreamPipesClient streamPipesClient) {
    return extractSharedConfig(extractor, config, streamPipesClient, false);
  }

  private static <T extends OpcUaConfig> T extractSharedConfig(IParameterExtractor extractor,
                                                               T config,
                                                               IStreamPipesClient streamPipesClient,
                                                               boolean includeNodeSelection) {

    String selectedAlternativeConnection =
        extractor.selectedAlternativeInternalId(OPC_HOST_OR_URL.name());

    String selectedAlternativeAuthentication =
        extractor.selectedAlternativeInternalId(SharedUserConfiguration.USER_AUTHENTICATION);

    if (includeNodeSelection) {
      List<String> selectedNodeNames =
          extractor.selectedTreeNodesInternalNames(AVAILABLE_NODES.name(), String.class);
      config.setSelectedNodeNames(selectedNodeNames);
    } else {
      config.setSelectedNodeNames(Collections.emptyList());
    }

    String selectedSecurityMode = extractor.selectedSingleValueInternalName(
        SharedUserConfiguration.SECURITY_MODE,
        String.class
    );
    String selectedSecurityPolicy = extractor.selectedSingleValue(
        SharedUserConfiguration.SECURITY_POLICY,
        String.class
    );
    config.setSecurityConfig(
        new SecurityConfig(
            MessageSecurityMode.valueOf(selectedSecurityMode),
            SecurityPolicy.valueOf(selectedSecurityPolicy),
            streamPipesClient
        )
    );
    config.setStreamPipesClient(streamPipesClient);

    boolean useURL = selectedAlternativeConnection.equals(OPC_URL.name());
    if (useURL) {
      String serverAddress =
          extractor.singleValueParameter(OPC_SERVER_URL.name(), String.class);
      config.setOpcServerURL(OpcUaUtils.addOpcPrefixIfNotExists(serverAddress));
    } else {
      String serverAddress = OpcUaUtils.addOpcPrefixIfNotExists(
          extractor.singleValueParameter(OPC_SERVER_HOST.name(), String.class)
      );
      int port = extractor.singleValueParameter(OPC_SERVER_PORT.name(), int.class);
      config.setOpcServerURL(serverAddress + ":" + port);
    }

    if (selectedAlternativeAuthentication.equals(SharedUserConfiguration.USER_AUTHENTICATION_ANONYMOUS)) {
      config.setIdentityConfig(new AnonymousIdentityConfig());
    } else if (selectedAlternativeAuthentication.equals(USERNAME_GROUP.name())) {
      String username = extractor.singleValueParameter(USERNAME.name(), String.class);
      String password = extractor.secretValue(PASSWORD.name());
      config.setIdentityConfig(new UsernamePasswordIdentityConfig(username, password));
    } else {
      String privateKeyPem = extractor.secretValue(SharedUserConfiguration.X509_PRIVATE_KEY_PEM);
      String publicKeyPem = extractor.textParameter(SharedUserConfiguration.X509_PUBLIC_KEY_PEM);
      config.setIdentityConfig(new X509IdentityConfig(publicKeyPem, privateKeyPem));
    }

    return config;
  }
}
