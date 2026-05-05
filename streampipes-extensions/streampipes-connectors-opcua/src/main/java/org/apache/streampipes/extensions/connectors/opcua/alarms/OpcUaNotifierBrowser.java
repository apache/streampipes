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

import org.apache.streampipes.extensions.connectors.opcua.adapter.OpcUaNodeMetadataExtractor;
import org.apache.streampipes.model.staticproperty.TreeInputNode;

import org.eclipse.milo.opcua.sdk.client.AddressSpace;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.NodeIds;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseDirection;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OpcUaNotifierBrowser {

  private static final Logger LOG = LoggerFactory.getLogger(OpcUaNotifierBrowser.class);
  private static final String SELECTABLE_METADATA_KEY = "selectable";

  private final OpcUaClient client;

  public OpcUaNotifierBrowser(OpcUaClient client) {
    this.client = client;
  }

  public List<TreeInputNode> buildNodeTreeFromOrigin(String nextBaseNodeToResolve) throws UaException {
    var currentNodeId = nextBaseNodeToResolve == null
        ? NodeIds.ObjectsFolder
        : NodeId.parse(nextBaseNodeToResolve);

    return findChildren(currentNodeId);
  }

  private List<TreeInputNode> findChildren(NodeId nodeId) throws UaException {
    var options = AddressSpace.BrowseOptions.builder()
        .setBrowseDirection(BrowseDirection.Forward)
        .setReferenceType(NodeIds.HierarchicalReferences)
        .setIncludeSubtypes(true)
        .setNodeClassMask(Set.of(NodeClass.Object))
        .build();

    var resolvedChildren = new ArrayList<TreeInputNode>();

    for (ReferenceDescription reference : client.getAddressSpace().browse(nodeId, options)) {
      tryResolveChildNode(reference).ifPresent(childNode -> resolvedChildren.add(toTreeNode(childNode)));
    }

    return resolvedChildren.stream()
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

  private Optional<UaNode> tryResolveChildNode(ReferenceDescription reference) {
    try {
      var nodeId = reference.getNodeId().toNodeId(client.getNamespaceTable());
      if (nodeId.isEmpty()) {
        return Optional.empty();
      }

      return Optional.of(client.getAddressSpace().getNode(nodeId.get()));
    } catch (UaException e) {
      LOG.debug(
          "Skipping OPC UA notifier tree reference {} because the node cannot be resolved with the current security settings",
          reference.getNodeId(),
          e
      );
      return Optional.empty();
    }
  }

  private TreeInputNode toTreeNode(UaNode node) {
    var treeNode = new TreeInputNode();
    treeNode.setNodeName(node.getDisplayName().getText());
    treeNode.setInternalNodeName(node.getNodeId().toParseableString());
    treeNode.setDataNode(false);

    var metadata = new OpcUaNodeMetadataExtractor(client, node).extract();
    metadata.put(SELECTABLE_METADATA_KEY, isSelectableNotifierNode(node));
    treeNode.setNodeMetadata(metadata);

    return treeNode;
  }

  private boolean isSelectableNotifierNode(UaNode node) {
    try {
      var eventNotifier = node.readAttribute(AttributeId.EventNotifier);
      var value = eventNotifier.getValue() != null ? eventNotifier.getValue().getValue() : null;

      if (value instanceof UByte uByte) {
        return uByte.intValue() > 0;
      }

      if (value instanceof Number number) {
        return number.intValue() > 0;
      }

      return false;
    } catch (UaException e) {
      LOG.debug("Skipping EventNotifier inspection for node {}", node.getNodeId(), e);
      return false;
    }
  }
}
