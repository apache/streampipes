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

package org.apache.streampipes.connect.iiot.adapters.opcua;

import org.apache.streampipes.connect.iiot.adapters.opcua.configuration.SpOpcUaConfig;
import org.apache.streampipes.connect.iiot.adapters.opcua.utils.OpcUaTypes;
import org.apache.streampipes.model.staticproperty.TreeInputNode;

import org.eclipse.milo.opcua.sdk.client.AddressSpace;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OpcUaNodeBrowser {

  private final OpcUaClient client;
  private final SpOpcUaConfig spOpcConfig;

  private static final Logger LOG = LoggerFactory.getLogger(OpcUaNodeBrowser.class);

  public OpcUaNodeBrowser(OpcUaClient client,
                          SpOpcUaConfig spOpcUaClientConfig) {
    this.client = client;
    this.spOpcConfig = spOpcUaClientConfig;
  }

  public List<OpcNode> findNodes() throws UaException {
    var opcNodes = new ArrayList<OpcNode>();
    for (String selectedNodeName : this.spOpcConfig.getSelectedNodeNames()) {
      opcNodes.add(toOpcNode(selectedNodeName));
    }

    return opcNodes;
  }

  public List<OpcNode> findNodes(List<String> runtimeNameFilters) throws UaException {
    return findNodes()
        .stream()
        .filter(node -> runtimeNameFilters
            .stream()
            .noneMatch(f -> f.equals(node.getLabel())))
        .collect(Collectors.toList());
  }

  public List<TreeInputNode> buildNodeTreeFromOrigin() throws UaException, ExecutionException, InterruptedException {
    NodeId origin = spOpcConfig.getOriginNodeId();

    UaNode parentNode = getAddressSpace().getNode(origin);
    List<TreeInputNode> nodes = new ArrayList<>();
    TreeInputNode parentInput = new TreeInputNode();
    parentInput.setInternalNodeName(parentNode.getNodeId().toParseableString());
    parentInput.setDataNode(isDataNode(parentNode));
    parentInput.setNodeName(parentNode.getDisplayName().getText());
    buildTreeAsync(client, parentInput).get();
    nodes.add(parentInput);

    return nodes;
  }

  private OpcNode toOpcNode(String nodeName) throws UaException {
    AddressSpace addressSpace = getAddressSpace();
    NodeId nodeId = NodeId.parse(nodeName);
    UaNode node = addressSpace.getNode(nodeId);

    LOG.info("Using node of type {}", node.getNodeClass().toString());

    if (node instanceof UaVariableNode) {
      UInteger value = (UInteger) ((UaVariableNode) node).getDataType().getIdentifier();
      return new OpcNode(node.getDisplayName().getText(), OpcUaTypes.getType(value), node.getNodeId());
    }

    LOG.warn("Node {} not of type UaVariableNode", node.getDisplayName());

    throw new UaException(StatusCode.BAD, "Node is not of type BaseDataVariableTypeNode");
  }

  private CompletableFuture<Void> buildTreeAsync(OpcUaClient client, TreeInputNode tree) {
    NodeId nodeId = NodeId.parse(tree.getInternalNodeName());
    return client.getAddressSpace().browseNodesAsync(nodeId).thenCompose(nodes -> {
      nodes.forEach(node -> {
        TreeInputNode childNode = new TreeInputNode();
        childNode.setNodeName(node.getDisplayName().getText());
        childNode.setInternalNodeName(node.getNodeId().toParseableString());
        childNode.setDataNode(isDataNode(node));
        tree.addChild(childNode);

      });

      Stream<CompletableFuture<Void>> futures =
          tree.getChildren().stream().map(child -> buildTreeAsync(client, child));

      return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    });
  }

  private AddressSpace getAddressSpace() {
    return client.getAddressSpace();
  }

  private boolean isDataNode(UaNode node) {
    return (node.getNodeClass().equals(NodeClass.Variable) || (node.getNodeClass().equals(NodeClass.VariableType)))
        && node instanceof UaVariableNode;
  }
}
