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

package org.apache.streampipes.connect.iiot.adapters.opcua.configuration;

import org.apache.streampipes.connect.iiot.adapters.opcua.utils.OpcUaUtil;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;

import java.util.ArrayList;
import java.util.List;

public class SpOpcUaConfigBuilder {

  /**
   * Creates {@link SpOpcUaConfig}  instance in accordance with the given
   * {@link org.apache.streampipes.sdk.extractor.StaticPropertyExtractor}.
   *
   * @param extractor extractor for user inputs
   * @return {@link SpOpcUaConfig}  instance based on information from {@code extractor}
   */
  public static SpOpcUaConfig from(StaticPropertyExtractor extractor) {

    String selectedAlternativeConnection =
        extractor.selectedAlternativeInternalId(OpcUaUtil.OpcUaLabels.OPC_HOST_OR_URL.name());
    String selectedAlternativeAuthentication =
        extractor.selectedAlternativeInternalId(OpcUaUtil.OpcUaLabels.ACCESS_MODE.name());

    int namespaceIndex = extractor.singleValueParameter(OpcUaUtil.OpcUaLabels.NAMESPACE_INDEX.name(), int.class);
    String nodeId = extractor.singleValueParameter(OpcUaUtil.OpcUaLabels.NODE_ID.name(), String.class);

    boolean usePullMode = extractor.selectedAlternativeInternalId(OpcUaUtil.OpcUaLabels.ADAPTER_TYPE.name())
        .equals(OpcUaUtil.OpcUaLabels.PULL_MODE.name());
    boolean useURL = selectedAlternativeConnection.equals(OpcUaUtil.OpcUaLabels.OPC_URL.name());
    boolean unauthenticated = selectedAlternativeAuthentication.equals(OpcUaUtil.OpcUaLabels.UNAUTHENTICATED.name());

    Integer pullIntervalSeconds = null;
    if (usePullMode) {
      pullIntervalSeconds =
          extractor.singleValueParameter(OpcUaUtil.OpcUaLabels.PULLING_INTERVAL.name(), Integer.class);
    }

    List<String> selectedNodeNames =
        extractor.selectedTreeNodesInternalNames(OpcUaUtil.OpcUaLabels.AVAILABLE_NODES.name(), String.class, true);

    if (useURL && unauthenticated) {

      String serverAddress = extractor.singleValueParameter(OpcUaUtil.OpcUaLabels.OPC_SERVER_URL.name(), String.class);
      serverAddress = OpcUaUtil.formatServerAddress(serverAddress);

      return new SpOpcUaConfig(serverAddress, namespaceIndex, nodeId, pullIntervalSeconds, selectedNodeNames);

    } else if (!useURL && unauthenticated) {
      String serverAddress = extractor.singleValueParameter(OpcUaUtil.OpcUaLabels.OPC_SERVER_HOST.name(), String.class);
      serverAddress = OpcUaUtil.formatServerAddress(serverAddress);
      int port = extractor.singleValueParameter(OpcUaUtil.OpcUaLabels.OPC_SERVER_PORT.name(), int.class);

      return new SpOpcUaConfig(serverAddress, port, namespaceIndex, nodeId, pullIntervalSeconds, selectedNodeNames);
    } else {

      String username = extractor.singleValueParameter(OpcUaUtil.OpcUaLabels.USERNAME.name(), String.class);
      String password = extractor.secretValue(OpcUaUtil.OpcUaLabels.PASSWORD.name());

      if (useURL) {
        String serverAddress =
            extractor.singleValueParameter(OpcUaUtil.OpcUaLabels.OPC_SERVER_URL.name(), String.class);
        serverAddress = OpcUaUtil.formatServerAddress(serverAddress);

        return new SpOpcUaConfig(serverAddress, namespaceIndex, nodeId, username, password, pullIntervalSeconds,
            selectedNodeNames);
      } else {
        String serverAddress =
            extractor.singleValueParameter(OpcUaUtil.OpcUaLabels.OPC_SERVER_HOST.name(), String.class);
        serverAddress = OpcUaUtil.formatServerAddress(serverAddress);
        int port = extractor.singleValueParameter(OpcUaUtil.OpcUaLabels.OPC_SERVER_PORT.name(), int.class);

        return new SpOpcUaConfig(serverAddress, port, namespaceIndex, nodeId, username, password, pullIntervalSeconds,
            selectedNodeNames);
      }
    }
  }

  /***
   * Creates {@link SpOpcUaConfig}  instance in accordance with the given
   * {@link org.apache.streampipes.model.connect.adapter.AdapterDescription}
   * @param adapterDescription description of current adapter
   * @return {@link SpOpcUaConfig}  instance based on information from {@code adapterDescription}
   */
  public static SpOpcUaConfig from(AdapterDescription adapterDescription) {

    StaticPropertyExtractor extractor = StaticPropertyExtractor.from(adapterDescription.getConfig(),
        new ArrayList<>());

    return from(extractor);
  }
}
