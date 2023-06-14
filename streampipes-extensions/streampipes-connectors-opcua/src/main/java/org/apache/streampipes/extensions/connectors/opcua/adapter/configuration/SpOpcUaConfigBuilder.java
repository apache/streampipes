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

package org.apache.streampipes.extensions.connectors.opcua.adapter.configuration;

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.connectors.opcua.adapter.utils.OpcUaUtil;

import java.util.List;

import static org.apache.kafka.common.config.ConfigDef.Type.PASSWORD;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.ACCESS_MODE;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.ADAPTER_TYPE;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.AVAILABLE_NODES;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.NAMESPACE_INDEX;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.NODE_ID;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_HOST_OR_URL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_SERVER_HOST;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_SERVER_PORT;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_SERVER_URL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_URL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.PULLING_INTERVAL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.PULL_MODE;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.UNAUTHENTICATED;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.USERNAME;

public class SpOpcUaConfigBuilder {

  /**
   * Creates {@link SpOpcUaConfig}  instance in accordance with the given
   * {@link org.apache.streampipes.sdk.extractor.StaticPropertyExtractor}.
   *
   * @param extractor extractor for user inputs
   * @return {@link SpOpcUaConfig}  instance based on information from {@code extractor}
   */
  public static SpOpcUaConfig from(IStaticPropertyExtractor extractor) {

    String selectedAlternativeConnection =
        extractor.selectedAlternativeInternalId(OPC_HOST_OR_URL.name());
    String selectedAlternativeAuthentication =
        extractor.selectedAlternativeInternalId(ACCESS_MODE.name());

    boolean usePullMode = extractor.selectedAlternativeInternalId(ADAPTER_TYPE.name())
        .equals(PULL_MODE.name());
    boolean useURL = selectedAlternativeConnection.equals(OPC_URL.name());
    boolean unauthenticated = selectedAlternativeAuthentication.equals(UNAUTHENTICATED.name());

    Integer pullIntervalSeconds = null;
    if (usePullMode) {
      pullIntervalSeconds =
          extractor.singleValueParameter(PULLING_INTERVAL.name(), Integer.class);
    }

    List<String> selectedNodeNames =
        extractor.selectedTreeNodesInternalNames(AVAILABLE_NODES.name(), String.class, true);

    if (useURL && unauthenticated) {

      String serverAddress =
          extractor.singleValueParameter(OPC_SERVER_URL.name(), String.class);
      serverAddress = OpcUaUtil.formatServerAddress(serverAddress);

      return new SpOpcUaConfig(serverAddress,
          pullIntervalSeconds,
          selectedNodeNames);

    } else if (!useURL && unauthenticated) {
      String serverAddress =
          extractor.singleValueParameter(OPC_SERVER_HOST.name(), String.class);
      serverAddress = OpcUaUtil.formatServerAddress(serverAddress);
      int port = extractor.singleValueParameter(OPC_SERVER_PORT.name(), int.class);

      return new SpOpcUaConfig(serverAddress,
          port,
          pullIntervalSeconds,
          selectedNodeNames);
    } else {

      String username = extractor.singleValueParameter(USERNAME.name(), String.class);
      String password = extractor.secretValue(PASSWORD.name());

      if (useURL) {
        String serverAddress =
            extractor.singleValueParameter(OPC_SERVER_URL.name(), String.class);
        serverAddress = OpcUaUtil.formatServerAddress(serverAddress);

        return new SpOpcUaConfig(serverAddress,
            username,
            password,
            pullIntervalSeconds,
            selectedNodeNames);
      } else {
        String serverAddress =
            extractor.singleValueParameter(OPC_SERVER_HOST.name(), String.class);
        serverAddress = OpcUaUtil.formatServerAddress(serverAddress);
        int port = extractor.singleValueParameter(OPC_SERVER_PORT.name(), int.class);

        return new SpOpcUaConfig(serverAddress,
            port,
            username,
            password,
            pullIntervalSeconds,
            selectedNodeNames);
      }
    }
  }
}
