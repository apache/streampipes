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

import org.eclipse.milo.opcua.sdk.core.typetree.ObjectTypeTree;
import org.eclipse.milo.opcua.stack.core.NodeIds;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record OpcUaAlarmField(String outputField,
                       NodeId typeDefinitionId,
                       QualifiedName[] browsePath) {

  static OpcUaAlarmField fromBrowsePath(NodeId typeDefinitionId, List<QualifiedName> browsePath) {
    return new OpcUaAlarmField(toOutputFieldName(browsePath), typeDefinitionId, browsePath.toArray(QualifiedName[]::new));
  }

  static List<OpcUaAlarmField> fieldsForType(NodeId selectedEventTypeId, ObjectTypeTree objectTypeTree) {
    Map<String, OpcUaAlarmField> fields = new LinkedHashMap<>();

    baseEventFields().forEach(field -> fields.put(field.outputField(), field));

    if (isTypeOrSubtypeOf(selectedEventTypeId, NodeIds.ConditionType, objectTypeTree)) {
      conditionFields().forEach(field -> fields.put(field.outputField(), field));
    }

    if (isTypeOrSubtypeOf(selectedEventTypeId, NodeIds.AcknowledgeableConditionType, objectTypeTree)) {
      acknowledgeableConditionFields().forEach(field -> fields.put(field.outputField(), field));
    }

    if (isTypeOrSubtypeOf(selectedEventTypeId, NodeIds.AlarmConditionType, objectTypeTree)) {
      alarmConditionFields().forEach(field -> fields.put(field.outputField(), field));
    }

    return List.copyOf(fields.values());
  }

  static List<OpcUaAlarmField> additionalFieldsForType(NodeId selectedEventTypeId, ObjectTypeTree objectTypeTree) {
    var baseFieldNames = baseEventFields().stream().map(OpcUaAlarmField::outputField).collect(java.util.stream.Collectors.toSet());

    return fieldsForType(selectedEventTypeId, objectTypeTree).stream()
        .filter(field -> !baseFieldNames.contains(field.outputField()))
        .sorted(Comparator.comparing(OpcUaAlarmField::displayName, String.CASE_INSENSITIVE_ORDER))
        .toList();
  }

  static List<OpcUaAlarmField> selectedFieldsForType(NodeId selectedEventTypeId,
                                                     List<String> selectedAdditionalFieldNames,
                                                     ObjectTypeTree objectTypeTree) {
    var selectedAdditionalFields = Set.copyOf(selectedAdditionalFieldNames == null
        ? List.<String>of()
        : selectedAdditionalFieldNames);

    var fields = new LinkedHashMap<String, OpcUaAlarmField>();
    baseEventFields().forEach(field -> fields.put(field.outputField(), field));

    additionalFieldsForType(selectedEventTypeId, objectTypeTree).stream()
        .filter(field -> selectedAdditionalFields.contains(field.outputField()))
        .forEach(field -> fields.put(field.outputField(), field));

    return List.copyOf(fields.values());
  }

  static List<OpcUaAlarmField> baseEventFieldsOnly() {
    return baseEventFields();
  }

  String displayName() {
    return Arrays.stream(browsePath)
        .map(QualifiedName::getName)
        .collect(Collectors.joining(" / "));
  }

  private static String toOutputFieldName(List<QualifiedName> browsePath) {
    var pathNames = browsePath.stream().map(QualifiedName::getName).toList();

    if (pathNames.equals(List.of("AckedState", "Id"))) {
      return "acked";
    }
    if (pathNames.equals(List.of("ActiveState", "Id"))) {
      return "active";
    }
    if (pathNames.equals(List.of("ConfirmedState", "Id"))) {
      return "confirmed";
    }

    var combined = String.join("", pathNames);
    return Character.toLowerCase(combined.charAt(0)) + combined.substring(1);
  }

  private static boolean isTypeOrSubtypeOf(NodeId selectedEventTypeId,
                                           NodeId targetTypeId,
                                           ObjectTypeTree objectTypeTree) {
    return selectedEventTypeId.equals(targetTypeId) || objectTypeTree.isSubtypeOf(selectedEventTypeId, targetTypeId);
  }

  private static List<OpcUaAlarmField> baseEventFields() {
    return List.of(
        field("sourceName", NodeIds.BaseEventType, "SourceName"),
        field("severity", NodeIds.BaseEventType, "Severity"),
        field("sourceNode", NodeIds.BaseEventType, "SourceNode"),
        field("message", NodeIds.BaseEventType, "Message"),
        field("time", NodeIds.BaseEventType, "Time"),
        field("eventId", NodeIds.BaseEventType, "EventId"),
        field("eventType", NodeIds.BaseEventType, "EventType")
    );
  }

  private static List<OpcUaAlarmField> conditionFields() {
    return List.of(
        field("conditionName", NodeIds.ConditionType, "ConditionName"),
        field("retain", NodeIds.ConditionType, "Retain")
    );
  }

  private static List<OpcUaAlarmField> acknowledgeableConditionFields() {
    return List.of(
        field("acked", NodeIds.AcknowledgeableConditionType, "AckedState", "Id")
    );
  }

  private static List<OpcUaAlarmField> alarmConditionFields() {
    return List.of(
        field("active", NodeIds.AlarmConditionType, "ActiveState", "Id")
    );
  }

  private static OpcUaAlarmField field(String outputField,
                                       NodeId typeDefinitionId,
                                       String... browsePath) {
    return new OpcUaAlarmField(outputField, typeDefinitionId, qualifiedNames(browsePath));
  }

  private static QualifiedName[] qualifiedNames(String... elements) {
    var qualifiedNames = new QualifiedName[elements.length];
    for (int i = 0; i < elements.length; i++) {
      qualifiedNames[i] = new QualifiedName(0, elements[i]);
    }
    return qualifiedNames;
  }
}
