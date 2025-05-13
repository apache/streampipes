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

import org.apache.streampipes.extensions.connectors.opcua.config.OpcUaAdapterConfig;
import org.apache.streampipes.extensions.connectors.opcua.model.node.BasicVariableNodeInfo;
import org.apache.streampipes.extensions.connectors.opcua.model.node.OpcUaNode;
import org.apache.streampipes.extensions.connectors.opcua.model.OpcUaNodeFactory;
import org.apache.streampipes.model.staticproperty.TreeInputNode;

import org.eclipse.milo.opcua.sdk.client.AddressSpace;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.UaClient;
import org.eclipse.milo.opcua.sdk.client.model.nodes.variables.BaseDataVariableTypeNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.UaRuntimeException;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class OpcUaNodeBrowser {

  private final OpcUaClient client;
  private final OpcUaAdapterConfig spOpcConfig;

  private static final Logger LOG = LoggerFactory.getLogger(OpcUaNodeBrowser.class);

  public OpcUaNodeBrowser(
      OpcUaClient client,
      OpcUaAdapterConfig spOpcUaClientConfig
  ) {
    this.client = client;
    this.spOpcConfig = spOpcUaClientConfig;
  }

  public OpcUaNodeProvider makeNodeProvider(List<String> runtimeNameFilters) throws UaException {
    var opcNodes = new ArrayList<OpcUaNode>();
    for (String selectedNodeName : this.spOpcConfig.getSelectedNodeNames()) {
      opcNodes.add(toOpcNode(selectedNodeName, runtimeNameFilters));
    }

    return new OpcUaNodeProvider(opcNodes);
  }

  public List<TreeInputNode> buildNodeTreeFromOrigin(String nextBaseNodeToResolve)
      throws UaException, ExecutionException, InterruptedException {

    var requestsRootNode = Objects.isNull(nextBaseNodeToResolve);
    var currentNodeId = requestsRootNode
        ? Identifiers.RootFolder : NodeId.parse(nextBaseNodeToResolve);

    return findChildren(client, currentNodeId);
  }

  private OpcUaNode toOpcNode(String nodeName,
                              List<String> runtimeNamesToDelete) throws UaException {
    AddressSpace addressSpace = getAddressSpace();

    NodeId nodeId;
    try {
      nodeId = NodeId.parse(nodeName);
    } catch (UaRuntimeException e) {
      throw new UaException(
          StatusCode.BAD.getValue(), "Node ID " + nodeName + " is not in the correct format. "
          + "The correct format is `ns=<namespaceIndex>;<identifierType>=<identifier>`.", e);
    }

    UaNode node;
    try {
      node = addressSpace.getNode(nodeId);
    } catch (UaException e) {
      throw new UaException(
          StatusCode.BAD.getValue(),
          "Node with ID " + nodeId + " is not present in the OPC UA server.", e
      );
    }

    LOG.info(
        "Using node of type {}",
        node.getNodeClass()
            .toString()
    );

    if (node instanceof BaseDataVariableTypeNode) {
      var nodeInfo = new BasicVariableNodeInfo((BaseDataVariableTypeNode) node, spOpcConfig.getNamingStrategy());
      return OpcUaNodeFactory.createOpcUaNode(nodeInfo, runtimeNamesToDelete);
    }

    LOG.warn("Node {} not of type UaVariableNode", node.getDisplayName());

    throw new UaException(StatusCode.BAD, "Node is not of type BaseDataVariableTypeNode");
  }

  private List<TreeInputNode> findChildren(
      UaClient client,
      NodeId nodeId
  ) throws UaException {
    return client
        .getAddressSpace()
        .browseNodes(nodeId)
        .stream()
        .map(node -> {
          TreeInputNode childNode = new TreeInputNode();
          childNode.setNodeName(node.getDisplayName()
              .getText());
          childNode.setInternalNodeName(node.getNodeId()
              .toParseableString());
          childNode.setDataNode(isDataNode(node));
          childNode.setNodeMetadata(new OpcUaNodeMetadataExtractor(client, node).extract());
          return childNode;
        })
        .collect(Collectors.toList());
  }


  private AddressSpace getAddressSpace() {
    return client.getAddressSpace();
  }

  private boolean isDataNode(UaNode node) {
    return (
        node.getNodeClass()
            .equals(NodeClass.Variable) || (
            node.getNodeClass()
                .equals(NodeClass.VariableType)
        )
    )
        && node instanceof UaVariableNode;
  }

}
