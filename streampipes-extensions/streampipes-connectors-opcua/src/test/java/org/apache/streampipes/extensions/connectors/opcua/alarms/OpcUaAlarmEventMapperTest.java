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

import org.eclipse.milo.opcua.sdk.core.typetree.ObjectType;
import org.eclipse.milo.opcua.sdk.core.typetree.ObjectTypeTree;
import org.eclipse.milo.opcua.stack.core.NodeIds;
import org.eclipse.milo.opcua.stack.core.encoding.DefaultEncodingContext;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.FilterOperator;
import org.eclipse.milo.opcua.stack.core.types.structured.ContentFilterElement;
import org.eclipse.milo.opcua.stack.core.types.structured.LiteralOperand;
import org.eclipse.milo.opcua.stack.core.util.Tree;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpcUaAlarmEventMapperTest {

  @Test
  void derivesBaseEventFieldsForBaseEventType() {
    var fields = OpcUaAlarmField.fieldsForType(NodeIds.BaseEventType, makeObjectTypeTree());

    assertTrue(containsField(fields, "sourceName"));
    assertTrue(containsField(fields, "severity"));
    assertTrue(containsField(fields, "message"));
    assertFalse(containsField(fields, "conditionName"));
    assertFalse(containsField(fields, "acked"));
    assertFalse(containsField(fields, "active"));
  }

  @Test
  void derivesAdditionalFieldsForAlarmConditionSubtypes() {
    var fields = OpcUaAlarmField.fieldsForType(NodeIds.AlarmConditionType, makeObjectTypeTree());

    assertTrue(containsField(fields, "sourceName"));
    assertTrue(containsField(fields, "conditionName"));
    assertTrue(containsField(fields, "retain"));
    assertTrue(containsField(fields, "acked"));
    assertTrue(containsField(fields, "active"));
  }

  @Test
  void selectsBaseFieldsPlusConfiguredAdditionalFieldsOnly() {
    var fields = OpcUaAlarmField.selectedFieldsForType(
        NodeIds.AlarmConditionType,
        List.of("conditionName", "active"),
        makeObjectTypeTree()
    );

    assertTrue(containsField(fields, "sourceName"));
    assertTrue(containsField(fields, "message"));
    assertTrue(containsField(fields, "conditionName"));
    assertTrue(containsField(fields, "active"));
    assertFalse(containsField(fields, "retain"));
    assertFalse(containsField(fields, "acked"));
  }

  @Test
  void derivesFieldNameAndDisplayNameFromBrowsePath() {
    var activeField = OpcUaAlarmField.fromBrowsePath(
        NodeIds.AlarmConditionType,
        List.of(new QualifiedName(0, "ActiveState"), new QualifiedName(0, "Id"))
    );
    var limitField = OpcUaAlarmField.fromBrowsePath(
        NodeIds.AlarmConditionType,
        List.of(new QualifiedName(0, "HighLimit"))
    );

    assertEquals("active", activeField.outputField());
    assertEquals("ActiveState / Id", activeField.displayName());
    assertEquals("highLimit", limitField.outputField());
    assertEquals("HighLimit", limitField.displayName());
  }

  @Test
  void mapsEventValuesUsingDerivedFieldOrder() {
    var mapper = new OpcUaAlarmEventMapper(
        NodeIds.AlarmConditionType,
        OpcUaAlarmField.selectedFieldsForType(
            NodeIds.AlarmConditionType,
            List.of("conditionName", "retain", "acked", "active"),
            makeObjectTypeTree()
        )
    );
    var now = DateTime.now();

    var event = mapper.toEvent(new Variant[] {
        new Variant("Pump 1"),
        new Variant(700),
        new Variant(new NodeId(2, "Pump_1")),
        new Variant(LocalizedText.english("Pressure too low")),
        new Variant(now),
        new Variant(ByteString.of(new byte[] {1, 2, 3})),
        new Variant(new NodeId(0, 2915)),
        new Variant(false),
        new Variant(true),
        new Variant("LowPressure"),
        new Variant(true)
    });

    assertEquals("Pump 1", event.get("sourceName"));
    assertEquals(700, event.get("severity"));
    assertEquals("ns=2;s=Pump_1", event.get("sourceNode"));
    assertEquals("Pressure too low", event.get("message"));
    assertEquals(now.getJavaTime(), event.get("time"));
    assertEquals(Base64.getEncoder().encodeToString(new byte[] {1, 2, 3}), event.get("eventId"));
    assertEquals("i=2915", event.get("eventType"));
    assertEquals("LowPressure", event.get("conditionName"));
    assertEquals(true, event.get("retain"));
    assertEquals(false, event.get("acked"));
    assertEquals(true, event.get("active"));
  }

  @Test
  void addsServerSideWhereClauseForSelectedEventType() {
    var mapper = new OpcUaAlarmEventMapper(
        NodeIds.AlarmConditionType,
        OpcUaAlarmField.fieldsForType(NodeIds.AlarmConditionType, makeObjectTypeTree())
    );

    var filter = mapper.makeEventFilter(DefaultEncodingContext.INSTANCE);

    assertNotNull(filter.getWhereClause());
    assertNotNull(filter.getWhereClause().getElements());
    assertEquals(1, filter.getWhereClause().getElements().length);

    ContentFilterElement element = filter.getWhereClause().getElements()[0];
    assertEquals(FilterOperator.OfType, element.getFilterOperator());
    assertNotNull(element.getFilterOperands());
    assertEquals(1, element.getFilterOperands().length);

    var operand = (LiteralOperand) element.getFilterOperands()[0].decode(DefaultEncodingContext.INSTANCE);
    assertEquals(NodeIds.AlarmConditionType, operand.getValue().getValue());
  }

  private boolean containsField(List<OpcUaAlarmField> fields, String outputField) {
    return fields.stream().anyMatch(field -> field.outputField().equals(outputField));
  }

  private ObjectTypeTree makeObjectTypeTree() {
    Tree<ObjectType> baseEventType = new Tree<>(null, objectType(NodeIds.BaseEventType, "BaseEventType"));
    Tree<ObjectType> conditionType = baseEventType.addChild(objectType(NodeIds.ConditionType, "ConditionType"));
    Tree<ObjectType> acknowledgeableConditionType = conditionType.addChild(
        objectType(NodeIds.AcknowledgeableConditionType, "AcknowledgeableConditionType")
    );
    acknowledgeableConditionType.addChild(objectType(NodeIds.AlarmConditionType, "AlarmConditionType"));

    return new ObjectTypeTree(baseEventType);
  }

  private ObjectType objectType(NodeId nodeId, String browseName) {
    return new ObjectType() {
      @Override
      public QualifiedName getBrowseName() {
        return new QualifiedName(0, browseName);
      }

      @Override
      public NodeId getNodeId() {
        return nodeId;
      }

      @Override
      public Boolean isAbstract() {
        return false;
      }
    };
  }
}
