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

import org.apache.streampipes.extensions.api.extractor.IParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaUtil;

import java.util.List;

import static org.apache.kafka.common.config.ConfigDef.Type.PASSWORD;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.ACCESS_MODE;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.ADAPTER_TYPE;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.AVAILABLE_NODES;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_HOST_OR_URL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_SERVER_HOST;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_SERVER_PORT;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_SERVER_URL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_URL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.PULLING_INTERVAL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.PULL_MODE;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.UNAUTHENTICATED;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.USERNAME;

public class SpOpcUaConfigExtractor {

  /**
   * Creates {@link OpcUaAdapterConfig}  instance in accordance with the given
   * {@link org.apache.streampipes.sdk.extractor.StaticPropertyExtractor}.
   *
   * @param extractor extractor for user inputs
   * @return {@link OpcUaAdapterConfig}  instance based on information from {@code extractor}
   */
  public static OpcUaAdapterConfig extractAdapterConfig(IStaticPropertyExtractor extractor) {
    var config = extractSharedConfig(extractor, new OpcUaAdapterConfig());
    boolean usePullMode = extractor.selectedAlternativeInternalId(ADAPTER_TYPE.name())
        .equals(PULL_MODE.name());

    if (usePullMode) {
      Integer pullIntervalSeconds =
          extractor.singleValueParameter(PULLING_INTERVAL.name(), Integer.class);

      config.setPullIntervalMilliSeconds(pullIntervalSeconds);
    }

    return config;
  }

  public static OpcUaConfig extractSinkConfig(IParameterExtractor<?> extractor) {
    return extractSharedConfig(extractor, new OpcUaConfig());
  }

  public static <T extends OpcUaConfig> T extractSharedConfig(IParameterExtractor<?> extractor,
                                                               T config) {

    String selectedAlternativeConnection =
        extractor.selectedAlternativeInternalId(OPC_HOST_OR_URL.name());
    String selectedAlternativeAuthentication =
        extractor.selectedAlternativeInternalId(ACCESS_MODE.name());
    List<String> selectedNodeNames =
        extractor.selectedTreeNodesInternalNames(AVAILABLE_NODES.name(), String.class, true);

    config.setSelectedNodeNames(selectedNodeNames);

    boolean useURL = selectedAlternativeConnection.equals(OPC_URL.name());
    boolean unauthenticated = selectedAlternativeAuthentication.equals(UNAUTHENTICATED.name());

    if (useURL) {
      String serverAddress =
          extractor.singleValueParameter(OPC_SERVER_URL.name(), String.class);
      config.setOpcServerURL(OpcUaUtil.formatServerAddress(serverAddress));
    } else {
      String serverAddress = OpcUaUtil.formatServerAddress(
          extractor.singleValueParameter(OPC_SERVER_HOST.name(), String.class)
      );
      int port = extractor.singleValueParameter(OPC_SERVER_PORT.name(), int.class);
      config.setOpcServerURL(serverAddress + ":" + port);
    }

    if (unauthenticated) {
      config.setUnauthenticated(true);
    } else {
      String username = extractor.singleValueParameter(USERNAME.name(), String.class);
      String password = extractor.secretValue(PASSWORD.name());

      config.setUsername(username);
      config.setPassword(password);
      config.setUnauthenticated(false);
    }

    return config;
  }
}
