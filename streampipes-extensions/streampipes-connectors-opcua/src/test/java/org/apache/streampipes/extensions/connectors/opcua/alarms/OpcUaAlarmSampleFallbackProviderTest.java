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

import org.eclipse.milo.opcua.stack.core.NodeIds;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpcUaAlarmSampleFallbackProviderTest {

  private final OpcUaAlarmSampleFallbackProvider provider = new OpcUaAlarmSampleFallbackProvider();

  @Test
  void createsBooleanPlaceholderForBooleanFields() {
    var sampleValue = provider.syntheticValue(NodeIds.Boolean, -1, "retain");

    assertEquals(true, sampleValue);
  }

  @Test
  void createsNumericPlaceholderForNumericFields() {
    var sampleValue = provider.syntheticValue(NodeIds.UInt32, -1, "severity");

    assertEquals(1, sampleValue);
  }

  @Test
  void createsTimestampPlaceholderForDateTimeFields() {
    var sampleValue = provider.syntheticValue(NodeIds.DateTime, -1, "time");

    assertInstanceOf(Long.class, sampleValue);
  }

  @Test
  void createsArrayPlaceholderForArrayFields() {
    var sampleValue = provider.syntheticValue(NodeIds.String, 1, "states");

    assertEquals(List.of("states"), sampleValue);
  }

  @Test
  void createsTypeMarkerForUnknownCustomTypes() {
    NodeId customType = new NodeId(2, 9001);

    var sampleValue = provider.syntheticValue(customType, -1, "customPayload");

    assertInstanceOf(Map.class, sampleValue);
    assertEquals("ns=2;i=9001", ((Map<?, ?>) sampleValue).get("_type"));
  }

  @Test
  void usesFieldSpecificDefaultsWhenDataTypeIsMissing() {
    var sampleValue = provider.syntheticValue(null, -1, "message");

    assertEquals("Sample OPC UA event", sampleValue);
  }

  @Test
  void usesBooleanPreviewForTwoStateFields() {
    var field = OpcUaAlarmField.fromTwoStateIdBrowsePath(
        NodeIds.ConditionType,
        new NodeId(0, 1234),
        List.of(
            new org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName(0, "EnabledState"),
            new org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName(0, "Id")
        )
    );

    var sampleValue = provider.buildSampleValue(null, field);

    assertTrue((Boolean) sampleValue);
  }
}
