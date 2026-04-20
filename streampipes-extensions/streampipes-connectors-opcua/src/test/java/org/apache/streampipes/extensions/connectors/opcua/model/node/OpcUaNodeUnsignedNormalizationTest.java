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

package org.apache.streampipes.extensions.connectors.opcua.model.node;

import org.eclipse.milo.opcua.stack.core.types.builtin.Matrix;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.ULong;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpcUaNodeUnsignedNormalizationTest {

  @Test
  void scalarNodeNormalizesUnsignedByte() {
    assertScalarValue(UByte.valueOf(255), 255);
  }

  @Test
  void scalarNodeNormalizesUnsignedShort() {
    assertScalarValue(UShort.valueOf(65535), 65535);
  }

  @Test
  void scalarNodeNormalizesUnsignedInteger() {
    assertScalarValue(UInteger.valueOf(4294967295L), 4294967295L);
  }

  @Test
  void scalarNodeNormalizesUnsignedLongToBigInteger() {
    assertScalarValue(ULong.MAX, -1L);
  }

  @Test
  void scalarNodeNormalizesUnsignedValuesInMatrix() {
    var event = new HashMap<String, Object>();

    new ScalarOpcUaNode(mockNodeInfo()).addToEvent(
        null,
        event,
        new Variant(new Matrix(new ULong[][] {{ULong.MAX}}))
    );

    assertEquals(
        Map.of(
            "testNode",
            Map.of(
                "elements",
                List.of(-1L),
                "dimensions",
                List.of(1, 1)
            )
        ),
        event
    );
  }

  @Test
  void structuredNodeNormalizesUnsignedByte() {
    assertStructuredValue(UByte.valueOf(255), 255);
  }

  @Test
  void structuredNodeNormalizesUnsignedShort() {
    assertStructuredValue(UShort.valueOf(65535), 65535);
  }

  @Test
  void structuredNodeNormalizesUnsignedInteger() {
    assertStructuredValue(UInteger.valueOf(4294967295L), 4294967295L);
  }

  @Test
  void structuredNodeNormalizesUnsignedLongToBigInteger() {
    assertStructuredValue(ULong.MAX, -1L);
  }

  @Test
  void structuredNodeNormalizesUnsignedValuesInArray() {
    var event = new HashMap<String, Object>();

    new StructuredOpcUaNode(mockNodeInfo()).addToEvent(
        null,
        event,
        new Variant(new ULong[] {ULong.MAX, ULong.valueOf(1L)})
    );

    assertEquals(
        List.of(-1L, 1L),
        event.get("testNode")
    );
  }

  private void assertScalarValue(Object inputValue, Object expectedValue) {
    var event = new HashMap<String, Object>();

    new ScalarOpcUaNode(mockNodeInfo()).addToEvent(null, event, new Variant(inputValue));

    assertEquals(expectedValue, event.get("testNode"));
  }

  private void assertStructuredValue(Object inputValue, Object expectedValue) {
    var event = new HashMap<String, Object>();

    new StructuredOpcUaNode(mockNodeInfo()).addToEvent(null, event, new Variant(inputValue));

    assertEquals(expectedValue, event.get("testNode"));
  }

  private BasicVariableNodeInfo mockNodeInfo() {
    var nodeInfo = mock(BasicVariableNodeInfo.class);
    when(nodeInfo.getDesiredName("")).thenReturn("testNode");
    when(nodeInfo.getBaseNodeName()).thenReturn("testNode");
    return nodeInfo;
  }
}
