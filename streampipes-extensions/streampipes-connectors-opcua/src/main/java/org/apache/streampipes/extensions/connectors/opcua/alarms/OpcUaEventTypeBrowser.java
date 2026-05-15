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

package org.apache.streampipes.extensions.connectors.opcua.alarms;

import org.apache.streampipes.model.staticproperty.TreeInputNode;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.core.typetree.ObjectType;
import org.eclipse.milo.opcua.sdk.core.typetree.ObjectTypeTree;
import org.eclipse.milo.opcua.stack.core.NodeIds;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.util.Tree;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OpcUaEventTypeBrowser {

  private static final String SELECTABLE_METADATA_KEY = "selectable";

  private final ObjectTypeTree objectTypeTree;

  public OpcUaEventTypeBrowser(OpcUaClient client) throws UaException {
    this.objectTypeTree = client.readObjectTypeTree();
  }

  public List<TreeInputNode> buildNodeTreeFromOrigin(String nextBaseNodeToResolve) {
    Tree<ObjectType> currentNode = nextBaseNodeToResolve == null
        ? objectTypeTree.getTreeNode(NodeIds.BaseEventType)
        : objectTypeTree.getTreeNode(NodeId.parse(nextBaseNodeToResolve));

    if (currentNode == null) {
      return List.of();
    }

    if (nextBaseNodeToResolve == null) {
      return List.of(toTreeNode(currentNode));
    }

    return currentNode.getChildren().stream()
        .map(this::toTreeNode)
        .collect(Collectors.toMap(
            TreeInputNode::getInternalNodeName,
            node -> node,
            (existing, duplicate) -> existing,
            LinkedHashMap::new
        ))
        .values()
        .stream()
        .sorted(Comparator.comparing(TreeInputNode::getNodeName, String.CASE_INSENSITIVE_ORDER))
        .toList();
  }

  private TreeInputNode toTreeNode(Tree<ObjectType> treeNode) {
    var objectType = treeNode.getValue();
    var node = new TreeInputNode();
    node.setNodeName(objectType.getBrowseName().getName());
    node.setInternalNodeName(objectType.getNodeId().toParseableString());
    node.setDataNode(false);

    Map<String, Object> metadata = new LinkedHashMap<>();
    metadata.put("Node ID", objectType.getNodeId().toParseableString());
    metadata.put("BrowseName", objectType.getBrowseName().getName());
    metadata.put("NodeClass", "ObjectType");
    metadata.put("Abstract", String.valueOf(Boolean.TRUE.equals(objectType.isAbstract())));
    metadata.put(SELECTABLE_METADATA_KEY, true);
    node.setNodeMetadata(metadata);

    return node;
  }
}
