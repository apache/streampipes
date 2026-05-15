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

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaObjectNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OpcUaNodeMetadataExtractor {
  private final OpcUaClient client;
  private final UaNode node;

  private final Map<String, Object> metadata;

  public OpcUaNodeMetadataExtractor(OpcUaClient client, UaNode node) {
    this.client = client;
    this.node = node;
    this.metadata = new LinkedHashMap<>();
  }

  public Map<String, Object> extract() {
    extractNodeId(node);
    extractNamespaceIndex();
    extractNodeClass();
    extractDescription();
    extractBrowseName();
    extractDisplayName();
    extractTypeDefinition();

    if (node instanceof UaVariableNode) {
      extractValue();
      try {
        var dataTypeNodeId = ((UaVariableNode) node).getDataType();
        var dataTypeNode = client.getAddressSpace().getNode(dataTypeNodeId);
        var value = client.readValue(0, TimestampsToReturn.Both, node.getNodeId());

        extractSourceTime(value);
        extractServerTime(value);
        extractStatusCode(value);
        extractDataType(dataTypeNode);
        extractDataTypeNodeId(dataTypeNodeId);
        extractValueRank((UaVariableNode) node);
        extractArrayDimensions((UaVariableNode) node);
        extractAccessLevel((UaVariableNode) node);
        extractUserAccessLevel((UaVariableNode) node);
        extractMinimumSamplingInterval((UaVariableNode) node);
        extractHistorizing((UaVariableNode) node);
      } catch (UaException e) {
        throw new RuntimeException(e);
      }
    }

    return metadata;
  }

  public void extractNodeId(UaNode node) {
    if (node != null && node.getNodeId() != null) {
      add("Node ID", node.getNodeId().toParseableString());
    } else {
      add("Node ID", "N/A");
    }
  }

  public void extractDataTypeNodeId(NodeId dataTypeNodeId) {
    if (dataTypeNodeId != null) {
      add("Data Type Node ID", dataTypeNodeId.toParseableString());
    } else {
      add("Data Type Node ID", "N/A");
    }
  }


  public void extractDescription() {
    if (node.getDescription() != null) {
      add("Description", node.getDescription().getText());
    } else {
      add("Description", "N/A");
    }
  }

  public void extractNamespaceIndex() {
    if (node.getNodeId() != null) {
      add("NamespaceIndex", node.getNodeId().getNamespaceIndex().toString());
    } else {
      add("NamespaceIndex", "N/A");
    }
  }

  public void extractNodeClass() {
    if (node.getNodeClass() != null) {
      add("NodeClass", node.getNodeClass().toString());
    } else {
      add("NodeClass", "");
    }
  }

  public void extractBrowseName() {
    if (node.getBrowseName() != null) {
      add("BrowseName", node.getBrowseName().getName());
    } else {
      add("BrowseName", "N/A");
    }
  }

  public void extractDisplayName() {
    if (node.getDisplayName() != null) {
      add("DisplayName", node.getDisplayName().getText());
    } else {
      add("DisplayName", "N/A");
    }
  }

  public void extractValue() {
    if (node instanceof UaVariableNode) {
      var v1 = ((UaVariableNode) node).getValue();
      if (v1 != null) {
        var v2 = v1.getValue();
        if (v2 != null) {
          add("Value", String.valueOf(v2.getValue()));
        }
      }
    }
  }

  public void extractValueRank(UaVariableNode node) {
    if (node == null || node.getValueRank() == null) {
      add("ValueRank", "N/A");
      return;
    }

    add("ValueRank", formatValueRank(node.getValueRank()));
  }

  public void extractArrayDimensions(UaVariableNode node) {
    if (node == null || node.getArrayDimensions() == null || node.getArrayDimensions().length == 0) {
      add("ArrayDimensions", "N/A");
      return;
    }

    add(
        "ArrayDimensions",
        Arrays.stream(node.getArrayDimensions())
            .map(String::valueOf)
            .toList()
            .toString()
    );
  }

  public void extractAccessLevel(UaVariableNode node) {
    add("AccessLevel", formatUnsignedByte(node == null ? null : node.getAccessLevel()));
  }

  public void extractUserAccessLevel(UaVariableNode node) {
    add("UserAccessLevel", formatUnsignedByte(node == null ? null : node.getUserAccessLevel()));
  }

  public void extractMinimumSamplingInterval(UaVariableNode node) {
    if (node == null || node.getMinimumSamplingInterval() == null) {
      add("MinimumSamplingInterval", "N/A");
    } else {
      add("MinimumSamplingInterval", String.valueOf(node.getMinimumSamplingInterval()));
    }
  }

  public void extractHistorizing(UaVariableNode node) {
    if (node == null || node.getHistorizing() == null) {
      add("Historizing", "N/A");
    } else {
      add("Historizing", String.valueOf(node.getHistorizing()));
    }
  }

  public void extractSourceTime(DataValue value) {
    add("SourceTime", dateTimeToString(value.getSourceTime()));
  }

  public void extractServerTime(DataValue value) {
    add("ServerTime", dateTimeToString(value.getServerTime()));
  }

  public void extractStatusCode(DataValue value) {
    var statusCode = value.getStatusCode();
    if (statusCode != null) {
      var statusCodeValue = statusCode.getValue();
      var lookup = StatusCodes.lookup(statusCodeValue);
      if (lookup.isPresent()) {
        var statusCodeName = lookup.get();
        if (statusCodeName.length > 0) {
          metadata.put("Status", statusCodeName[0]);
        }
      }
    }
  }

  public void extractDataType(UaNode dataTypeNode) {
    if (dataTypeNode.getDisplayName() != null) {
      metadata.put("DataType", dataTypeNode.getDisplayName().getText());
    } else {
      metadata.put("DataType", "");
    }
  }

  public void extractTypeDefinition() {
    try {
      if (node instanceof UaVariableNode uaVariableNode) {
        var typeDefinition = uaVariableNode.getTypeDefinition();
        addTypeDefinition(typeDefinition == null ? null : typeDefinition.getNodeId(), typeDefinition);
      } else if (node instanceof UaObjectNode uaObjectNode) {
        var typeDefinition = uaObjectNode.getTypeDefinition();
        addTypeDefinition(typeDefinition == null ? null : typeDefinition.getNodeId(), typeDefinition);
      }
    } catch (UaException e) {
      add("TypeDefinition", "N/A");
      add("TypeDefinition Node ID", "N/A");
    }
  }

  private String dateTimeToString(DateTime time) {
    if (time != null) {
      return time.getJavaDate().toString();
    } else {
      return "";
    }
  }

  public Map<String, Object> getMetadata() {
    return metadata;
  }

  private void add(String key, String value) {
    metadata.put(key, value);
  }

  private void addTypeDefinition(NodeId typeDefinitionId, UaNode typeDefinitionNode) {
    if (typeDefinitionNode != null && typeDefinitionNode.getDisplayName() != null) {
      add("TypeDefinition", typeDefinitionNode.getDisplayName().getText());
    } else {
      add("TypeDefinition", "N/A");
    }

    if (typeDefinitionId != null) {
      add("TypeDefinition Node ID", typeDefinitionId.toParseableString());
    } else {
      add("TypeDefinition Node ID", "N/A");
    }
  }

  private String formatUnsignedByte(UByte value) {
    if (value == null) {
      return "N/A";
    }

    var numericValue = value.intValue();
    var flags = describeAccessLevelFlags(numericValue);

    if (flags.isEmpty()) {
      return numericValue + " (None)";
    }

    return String.join(", ", flags) + " (" + numericValue + ")";
  }

  private String formatValueRank(Integer valueRank) {
    switch (valueRank) {
      case -3:
        return "-3 (Scalar or OneDimension)";
      case -2:
        return "-2 (Any)";
      case -1:
        return "-1 (Scalar)";
      case 0:
        return "0 (One or more dimensions)";
      case 1:
        return "1 (One dimension)";
      default:
        return valueRank + " (" + valueRank + " dimensions)";
    }
  }

  private List<String> describeAccessLevelFlags(int numericValue) {
    var flags = new java.util.ArrayList<String>();

    addAccessLevelFlag(flags, numericValue, 0x01, "CurrentRead");
    addAccessLevelFlag(flags, numericValue, 0x02, "CurrentWrite");
    addAccessLevelFlag(flags, numericValue, 0x04, "HistoryRead");
    addAccessLevelFlag(flags, numericValue, 0x08, "HistoryWrite");
    addAccessLevelFlag(flags, numericValue, 0x10, "SemanticChange");
    addAccessLevelFlag(flags, numericValue, 0x20, "StatusWrite");
    addAccessLevelFlag(flags, numericValue, 0x40, "TimestampWrite");

    return flags;
  }

  private void addAccessLevelFlag(List<String> flags, int numericValue, int mask, String label) {
    if ((numericValue & mask) != 0) {
      flags.add(label);
    }
  }

}
