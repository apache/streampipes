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

package org.apache.streampipes.extensions.connectors.opcua.adapter;

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.connectors.opcua.client.OpcUaClientProvider;
import org.apache.streampipes.extensions.connectors.opcua.config.SpOpcUaConfigExtractor;
import org.apache.streampipes.extensions.connectors.opcua.model.node.OpcUaNode;
import org.apache.streampipes.model.connect.guess.FieldStatusInfo;
import org.apache.streampipes.model.connect.guess.SampleData;
import org.apache.streampipes.sdk.builder.adapter.SampleDataBuilder;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OpcUaSchemaProvider {

  /***
   * OPC UA specific implementation of
   * @param extractor
   * @return guess schema
   * @throws AdapterException
   * @throws ParseException
   */
  public SampleData getSampleData(OpcUaClientProvider clientProvider,
                                   IAdapterParameterExtractor extractor,
                                   IStreamPipesClient streamPipesClient)
      throws AdapterException, ParseException {
    Map<String, Object> eventPreview = new HashMap<>();
    Map<String, FieldStatusInfo> fieldStatusInfos = new HashMap<>();

    var opcUaConfig = SpOpcUaConfigExtractor.extractAdapterConfig(
        extractor.getStaticPropertyExtractor(),
        streamPipesClient
    );
    try {
      var connectedClient = clientProvider.getClient(opcUaConfig);
      OpcUaNodeBrowser nodeBrowser =
          new OpcUaNodeBrowser(connectedClient.getClient(), opcUaConfig);
      var nodeProvider = nodeBrowser.makeNodeProvider();
      var selectedNodes = nodeProvider.getNodes();

      var nodeIds = selectedNodes.stream()
          .map(node -> node.nodeInfo().getNodeId())
          .collect(Collectors.toList());
      var returnValues = connectedClient.getClient()
          .readValues(0, TimestampsToReturn.Both, nodeIds);
      makeEventPreview(connectedClient.getClient(), selectedNodes, eventPreview, fieldStatusInfos, returnValues);



    } catch (Exception e) {
      throw new AdapterException("Could not guess schema for opc node:  " + e.getMessage(), e);
    } finally {
      clientProvider.releaseClient(opcUaConfig);
    }

    return SampleDataBuilder.create()
                     .sample(eventPreview)
        .fieldStatusInfos(fieldStatusInfos)
                     .build();
  }

  private static void makeEventPreview(
      OpcUaClient client,
      List<OpcUaNode> selectedNodes,
      Map<String, Object> eventPreview,
      Map<String, FieldStatusInfo> fieldStatusInfos,
      List<DataValue> dataValues
  ) {

    for (int i = 0; i < dataValues.size(); i++) {
      var dv = dataValues.get(i);
      var node = selectedNodes.get(i);
      if (StatusCode.GOOD.equals(dv.getStatusCode())) {
        var value = dv.getValue();
        node.addToEventPreview(client, eventPreview, fieldStatusInfos, value, FieldStatusInfo.good());
      } else {
        String additionalInfo = dv.getStatusCode() != null ? dv.getStatusCode()
            .toString() : "Status code is null";
        node.addToEventPreview(
            client,
            Map.of(),
            fieldStatusInfos,
            null,
            FieldStatusInfo.bad(additionalInfo));
      }
    }
  }
}
