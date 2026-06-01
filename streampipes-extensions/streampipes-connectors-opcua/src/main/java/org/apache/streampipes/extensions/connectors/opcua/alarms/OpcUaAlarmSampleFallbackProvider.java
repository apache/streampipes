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

import org.eclipse.milo.opcua.sdk.client.AddressSpace;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.NodeIds;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseDirection;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

class OpcUaAlarmSampleFallbackProvider {

  private static final Logger LOG = LoggerFactory.getLogger(OpcUaAlarmSampleFallbackProvider.class);
  private static final int VALUE_RANK_SCALAR = -1;

  Map<String, Object> buildFallbackSample(OpcUaClient client,
                                          OpcUaAlarmAdapterConfig config) throws UaException {
    var fieldProvider = new OpcUaEventFieldProvider(client);
    var selectedEventTypeNodeId = config.getEventTypeNodeId() == null || config.getEventTypeNodeId().isBlank()
        ? NodeIds.BaseEventType.toParseableString()
        : config.getEventTypeNodeId();

    var selectedFields = fieldProvider.buildSelectedFields(
        selectedEventTypeNodeId,
        config.getSelectedAdditionalFieldNames()
    );

    var sample = new LinkedHashMap<String, Object>();
    for (var field : selectedFields) {
      sample.put(field.outputField(), buildSampleValue(client, field));
    }

    return sample;
  }

  Object buildSampleValue(OpcUaClient client, OpcUaAlarmField field) {
    if (field.extractionMode() == OpcUaAlarmField.ExtractionMode.TWO_STATE_LOCALIZED_TEXT) {
      return true;
    }

    try {
      var declarationNode = resolveDeclarationNode(client, field.typeDefinitionId(), List.of(field.browsePath()));
      if (declarationNode instanceof UaVariableNode variableNode) {
        return syntheticValue(
            variableNode.getDataType(),
            variableNode.getValueRank(),
            field.outputField()
        );
      }
    } catch (Exception e) {
      LOG.debug("Could not resolve data type for OPC UA event preview field {}", field.outputField(), e);
    }

    return defaultValueForFieldName(field.outputField());
  }

  private UaNode resolveDeclarationNode(OpcUaClient client,
                                        NodeId startNodeId,
                                        List<QualifiedName> browsePath) throws UaException {
    UaNode currentNode = client.getAddressSpace().getNode(startNodeId);

    for (QualifiedName pathElement : browsePath) {
      currentNode = browseAggregateChildren(client, currentNode.getNodeId()).stream()
          .filter(child -> Objects.equals(child.getBrowseName(), pathElement))
          .findFirst()
          .orElse(null);

      if (currentNode == null) {
        return null;
      }
    }

    return currentNode;
  }

  private List<? extends UaNode> browseAggregateChildren(OpcUaClient client, NodeId nodeId) throws UaException {
    var options = AddressSpace.BrowseOptions.builder()
        .setBrowseDirection(BrowseDirection.Forward)
        .setReferenceType(NodeIds.Aggregates)
        .setIncludeSubtypes(true)
        .setNodeClassMask(Set.of(NodeClass.Object, NodeClass.Variable))
        .build();

    return client.getAddressSpace().browseNodes(nodeId, options);
  }

  Object syntheticValue(NodeId dataTypeId, Integer valueRank, String outputField) {
    var sampleValue = scalarSyntheticValue(dataTypeId, outputField);

    if (valueRank != null && valueRank != VALUE_RANK_SCALAR) {
      return List.of(sampleValue);
    }

    return sampleValue;
  }

  private Object scalarSyntheticValue(NodeId dataTypeId, String outputField) {
    if (dataTypeId == null) {
      return defaultValueForFieldName(outputField);
    }

    if (NodeIds.Boolean.equals(dataTypeId)) {
      return true;
    } else if (NodeIds.SByte.equals(dataTypeId)
        || NodeIds.Byte.equals(dataTypeId)
        || NodeIds.Int16.equals(dataTypeId)
        || NodeIds.UInt16.equals(dataTypeId)
        || NodeIds.Int32.equals(dataTypeId)
        || NodeIds.UInt32.equals(dataTypeId)
        || NodeIds.Int64.equals(dataTypeId)
        || NodeIds.UInt64.equals(dataTypeId)
        || NodeIds.Float.equals(dataTypeId)
        || NodeIds.Double.equals(dataTypeId)) {
      return 1;
    } else if (NodeIds.String.equals(dataTypeId)) {
      return sampleText(outputField);
    } else if (NodeIds.DateTime.equals(dataTypeId)) {
      return DateTime.now().getJavaTime();
    } else if (NodeIds.ByteString.equals(dataTypeId)) {
      return "AQID";
    } else if (NodeIds.NodeId.equals(dataTypeId)
        || NodeIds.ExpandedNodeId.equals(dataTypeId)) {
      return "ns=0;i=0";
    } else if (NodeIds.LocalizedText.equals(dataTypeId)) {
      return sampleText(outputField);
    } else if (NodeIds.Guid.equals(dataTypeId)) {
      return "00000000-0000-0000-0000-000000000000";
    } else if (NodeIds.XmlElement.equals(dataTypeId)) {
      return "<value>sample</value>";
    } else if (NodeIds.QualifiedName.equals(dataTypeId)) {
      return "0:" + outputField;
    } else if (NodeIds.StatusCode.equals(dataTypeId)) {
      return "Good (0)";
    } else if (NodeIds.Structure.equals(dataTypeId)
        || NodeIds.BaseDataType.equals(dataTypeId)
        || NodeIds.Number.equals(dataTypeId)
        || NodeIds.Integer.equals(dataTypeId)
        || NodeIds.UInteger.equals(dataTypeId)
        || NodeIds.Enumeration.equals(dataTypeId)) {
      return defaultValueForFieldName(outputField);
    }

    return Map.of("_type", dataTypeId.toParseableString());
  }

  private Object defaultValueForFieldName(String outputField) {
    return switch (outputField) {
      case "severity" -> 1;
      case "time" -> DateTime.now().getJavaTime();
      case "sourceNode", "eventType" -> "ns=0;i=0";
      case "eventId" -> "AQID";
      case "retain", "acked", "confirmed", "active" -> true;
      default -> sampleText(outputField);
    };
  }

  private String sampleText(String outputField) {
    return switch (outputField) {
      case "message" -> LocalizedText.english("Sample OPC UA event").getText();
      case "sourceName" -> "SampleSource";
      case "conditionName" -> "SampleCondition";
      default -> outputField;
    };
  }
}
