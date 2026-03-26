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

package org.apache.streampipes.integration.adapters.opcua;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class OpcUaNodeDiscovery {

  private OpcUaNodeDiscovery() {
  }

  public static Map<String, String> discoverDataTypeTestVariableNodes(String endpointUrl) throws Exception {
    OpcUaClient client = OpcUaClient.create(endpointUrl);
    client.connect();

    try {
      NodeId dataTypeTestNodeId = findNodeByBrowseName(
          client,
          Identifiers.RootFolder,
          "DataTypeTest",
          8,
          new LinkedHashSet<>()
      );

      assertNotNull(dataTypeTestNodeId, "Could not find DataTypeTest folder in OPC UA demo server");

      Map<String, String> variableNodes = new LinkedHashMap<>();
      collectVariableNodeIds(client, dataTypeTestNodeId, variableNodes, new LinkedHashSet<>());
      return variableNodes;
    } finally {
      client.disconnect();
    }
  }

  public static List<String> selectStructureNodes(Map<String, String> availableNodes) {
    List<String> preferredNames = List.of(
        "StructWithBuiltinArrayFields",
        "StructWithBuiltinMatrixFields",
        "StructWithBuiltinFields",
        "StructWithEnumFields",
        "StructWithOptionalFields",
        "StructWithStructureArrayFields",
        "StructWithStructureFields",
        "StructWithStructureMatrixFields"
    );

    List<String> selected = new ArrayList<>();
    for (String preferredName : preferredNames) {
      if (availableNodes.containsKey(preferredName)) {
        selected.add(availableNodes.get(preferredName));
      }
    }

    if (selected.size() < 3) {
      for (Map.Entry<String, String> entry : availableNodes.entrySet()) {
        if (entry.getKey().contains("Struct") && !selected.contains(entry.getValue())) {
          selected.add(entry.getValue());
        }
        if (selected.size() >= 3) {
          break;
        }
      }
    }

    if (selected.size() < 3) {
      for (String nodeId : availableNodes.values()) {
        if (!selected.contains(nodeId)) {
          selected.add(nodeId);
        }
        if (selected.size() >= 3) {
          break;
        }
      }
    }

    return selected;
  }

  private static NodeId findNodeByBrowseName(OpcUaClient client,
                                             NodeId startNodeId,
                                             String targetBrowseName,
                                             int maxDepth,
                                             Set<NodeId> visited) throws Exception {
    if (maxDepth < 0 || !visited.add(startNodeId)) {
      return null;
    }

    List<? extends UaNode> children = client.getAddressSpace().browseNodes(startNodeId);
    for (UaNode child : children) {
      if (targetBrowseName.equals(child.getBrowseName().getName())) {
        return child.getNodeId();
      }
    }

    for (UaNode child : children) {
      NodeId match = findNodeByBrowseName(client, child.getNodeId(), targetBrowseName, maxDepth - 1, visited);
      if (match != null) {
        return match;
      }
    }

    return null;
  }

  private static void collectVariableNodeIds(OpcUaClient client,
                                             NodeId currentNodeId,
                                             Map<String, String> result,
                                             Set<NodeId> visited) throws Exception {
    if (!visited.add(currentNodeId)) {
      return;
    }

    List<? extends UaNode> children = client.getAddressSpace().browseNodes(currentNodeId);
    for (UaNode child : children) {
      if (NodeClass.Variable.equals(child.getNodeClass())) {
        result.putIfAbsent(child.getBrowseName().getName(), child.getNodeId().toParseableString());
      }
      collectVariableNodeIds(client, child.getNodeId(), result, visited);
    }
  }
}

