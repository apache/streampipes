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

import org.apache.streampipes.model.staticproperty.Option;

import org.eclipse.milo.opcua.sdk.client.AddressSpace;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableTypeNode;
import org.eclipse.milo.opcua.sdk.core.typetree.ObjectType;
import org.eclipse.milo.opcua.sdk.core.typetree.ObjectTypeTree;
import org.eclipse.milo.opcua.stack.core.NodeIds;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseDirection;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.util.Tree;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class OpcUaEventFieldProvider {

  private final OpcUaClient client;
  private final ObjectTypeTree objectTypeTree;

  public OpcUaEventFieldProvider(OpcUaClient client) throws UaException {
    this.client = client;
    this.objectTypeTree = client.readObjectTypeTree();
  }

  public List<Option> buildAdditionalFieldOptions(String selectedEventTypeNodeId, List<Option> currentOptions) {
    var selectedFieldNames = currentOptions == null
        ? Set.<String>of()
        : currentOptions.stream()
            .filter(Option::isSelected)
            .map(Option::getInternalName)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

    return buildAdditionalFields(NodeId.parse(selectedEventTypeNodeId)).stream()
        .map(field -> {
          var option = new Option(field.displayName(), field.selectionId());
          option.setSelected(selectedFieldNames.contains(field.selectionId()));
          return option;
        })
        .toList();
  }

  public List<OpcUaAlarmField> buildSelectedFields(String selectedEventTypeNodeId,
                                                   List<String> selectedAdditionalFieldNames) {
    var selectedEventTypeId = NodeId.parse(selectedEventTypeNodeId);
    var selectedNames = Set.copyOf(selectedAdditionalFieldNames == null
        ? List.<String>of()
        : selectedAdditionalFieldNames);

    var fields = new LinkedHashMap<String, OpcUaAlarmField>();
    OpcUaAlarmField.fieldsForType(selectedEventTypeId, objectTypeTree)
        .forEach(field -> fields.put(field.outputField(), field));

    buildAdditionalFields(selectedEventTypeId).stream()
        .filter(field -> selectedNames.contains(field.selectionId()))
        .forEach(field -> fields.put(field.outputField(), field));

    return List.copyOf(fields.values());
  }

  private List<OpcUaAlarmField> buildAdditionalFields(NodeId selectedEventTypeId) {
    var fields = buildDeclaredFields(selectedEventTypeId);
    var standardFieldNames = OpcUaAlarmField.fieldsForType(selectedEventTypeId, objectTypeTree).stream()
        .map(OpcUaAlarmField::outputField)
        .collect(Collectors.toSet());

    return deduplicateAdditionalFields(fields.values().stream()
        .filter(field -> !standardFieldNames.contains(field.outputField()))
        .sorted(java.util.Comparator.comparing(OpcUaAlarmField::displayName, String.CASE_INSENSITIVE_ORDER))
        .toList());
  }

  static List<OpcUaAlarmField> deduplicateAdditionalFields(List<OpcUaAlarmField> fields) {
    return new ArrayList<>(fields.stream()
        .collect(Collectors.toMap(
            OpcUaAlarmField::outputField,
            field -> field,
            (existing, ignored) -> existing,
            LinkedHashMap::new
        ))
        .values());
  }

  private Map<String, OpcUaAlarmField> buildDeclaredFields(NodeId selectedEventTypeId) {
    Tree<ObjectType> selectedTypeNode = objectTypeTree.getTreeNode(selectedEventTypeId);
    if (selectedTypeNode == null) {
      return Map.of();
    }

    var fields = new LinkedHashMap<String, OpcUaAlarmField>();

    for (Tree<ObjectType> current = selectedTypeNode; current != null; current = current.getParent()) {
      collectInstanceDeclarations(current.getValue().getNodeId(), current.getValue().getNodeId(), List.of(), fields);
    }

    return fields;
  }

  private void collectInstanceDeclarations(NodeId currentNodeId,
                                           NodeId declaringTypeId,
                                           List<QualifiedName> currentPath,
                                           Map<String, OpcUaAlarmField> fields) {
    var currentNode = resolveNode(currentNodeId);

    for (UaNode childNode : browseAggregateChildren(currentNodeId)) {
      var nextPath = new ArrayList<>(currentPath);
      nextPath.add(childNode.getBrowseName());

      var nestedChildren = browseAggregateChildren(childNode.getNodeId());
      if (nestedChildren.isEmpty()) {
        var field = currentNode instanceof UaVariableNode currentVariableNode
            && isTwoStateIdSelection(currentVariableNode, childNode.getBrowseName())
            ? OpcUaAlarmField.fromTwoStateIdBrowsePath(declaringTypeId, childNode.getNodeId(), nextPath)
            : OpcUaAlarmField.fromBrowsePath(declaringTypeId, childNode.getNodeId(), nextPath);
        fields.putIfAbsent(field.selectionId(), field);
      } else {
        collectInstanceDeclarations(childNode.getNodeId(), declaringTypeId, nextPath, fields);
      }
    }
  }

  private UaNode resolveNode(NodeId nodeId) {
    try {
      return client.getAddressSpace().getNode(nodeId);
    } catch (UaException e) {
      return null;
    }
  }

  private boolean isTwoStateIdSelection(UaVariableNode variableNode, QualifiedName childBrowseName) {
    if (!"Id".equals(childBrowseName.getName())) {
      return false;
    }

    try {
      return isVariableTypeOrSubtypeOf(variableNode.getTypeDefinition(), NodeIds.TwoStateVariableType);
    } catch (UaException e) {
      return false;
    }
  }

  private boolean isVariableTypeOrSubtypeOf(UaVariableTypeNode variableTypeNode, NodeId targetTypeId) {
    UaVariableTypeNode current = variableTypeNode;

    while (current != null) {
      if (targetTypeId.equals(current.getNodeId())) {
        return true;
      }

      current = readSuperType(current);
    }

    return false;
  }

  private UaVariableTypeNode readSuperType(UaVariableTypeNode variableTypeNode) {
    try {
      var options = AddressSpace.BrowseOptions.builder()
          .setBrowseDirection(BrowseDirection.Inverse)
          .setReferenceType(NodeIds.HasSubtype)
          .setIncludeSubtypes(false)
          .setNodeClassMask(Set.of(NodeClass.VariableType))
          .build();

      return variableTypeNode.browseNodes(options).stream()
          .filter(UaVariableTypeNode.class::isInstance)
          .map(UaVariableTypeNode.class::cast)
          .findFirst()
          .orElse(null);
    } catch (UaException e) {
      return null;
    }
  }

  private List<? extends UaNode> browseAggregateChildren(NodeId nodeId) {
    try {
      var options = AddressSpace.BrowseOptions.builder()
          .setBrowseDirection(BrowseDirection.Forward)
          .setReferenceType(NodeIds.Aggregates)
          .setIncludeSubtypes(true)
          .setNodeClassMask(Set.of(NodeClass.Object, NodeClass.Variable))
          .build();

      return client.getAddressSpace().browseNodes(nodeId, options);
    } catch (UaException e) {
      return List.of();
    }
  }
}
