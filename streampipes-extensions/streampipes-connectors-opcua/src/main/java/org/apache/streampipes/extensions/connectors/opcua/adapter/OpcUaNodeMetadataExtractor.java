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
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class OpcUaNodeMetadataExtractor {
  private final OpcUaClient client;
  private final UaNode node;

  private final Map<String, Object> metadata;

  public OpcUaNodeMetadataExtractor(OpcUaClient client, UaNode node) {
    this.client = client;
    this.node = node;
    this.metadata = new HashMap<>();
  }

  public Map<String, Object> extract() {
    extractNamespaceIndex();
    extractNodeClass();
    extractDescription();
    extractBrowseName();
    extractDisplayName();

    if (node instanceof UaVariableNode) {
      extractValue();
      try {
        var dataTypeNodeId = ((UaVariableNode) node).getDataType();
        var dataTypeNode = client.getAddressSpace().getNode(dataTypeNodeId);
        var value = client.readValue(0, TimestampsToReturn.Both, node.getNodeId()).get();

        extractSourceTime(value);
        extractServerTime(value);
        extractStatusCode(value);
        extractDataType(dataTypeNode);
      } catch (UaException | ExecutionException | InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    return metadata;
  }


  public void extractDescription() {
    if (node.getDescription() != null) {
      add("Description", node.getDescription().getText());
    } else {
      add("Description", "");
    }
  }

  public void extractNamespaceIndex() {
    if (node.getNodeId() != null) {
      add("NamespaceIndex", node.getNodeId().getNamespaceIndex().toString());
    } else {
      add("NamespaceIndex", "");
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
      add("BrowseName", "");
    }
  }

  public void extractDisplayName() {
    if (node.getDisplayName() != null) {
      add("DisplayName", node.getDisplayName().getText());
    } else {
      add("DisplayName", "");
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

}
