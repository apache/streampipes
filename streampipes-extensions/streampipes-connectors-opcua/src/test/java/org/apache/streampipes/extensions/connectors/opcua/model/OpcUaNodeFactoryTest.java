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

package org.apache.streampipes.extensions.connectors.opcua.model;

import org.apache.streampipes.extensions.connectors.opcua.model.node.BasicVariableNodeInfo;
import org.apache.streampipes.extensions.connectors.opcua.model.node.ScalarOpcUaNode;
import org.apache.streampipes.extensions.connectors.opcua.model.node.StructuredOpcUaNode;

import org.eclipse.milo.opcua.sdk.core.nodes.VariableNode;
import org.eclipse.milo.opcua.stack.core.NodeIds;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Regression tests for #5244: vendor-specific subtypes of String (for example Siemens or Beckhoff
 * STRING types such as ns=3;i=3013) must be read as scalars, not as structures.
 */
class OpcUaNodeFactoryTest {

  private static final NodeId VENDOR_STRING_TYPE = new NodeId(3, Unsigned.uint(3013));

  @Test
  void vendorStringSubtypeWithStringValueIsScalar() {
    var node = OpcUaNodeFactory.createOpcUaNode(
        nodeInfo(VENDOR_STRING_TYPE), good(new Variant("Recipe A")));

    assertInstanceOf(ScalarOpcUaNode.class, node);
  }

  @Test
  void vendorStringSubtypeArrayIsScalar() {
    var node = OpcUaNodeFactory.createOpcUaNode(
        nodeInfo(VENDOR_STRING_TYPE), good(new Variant(new String[] {"a", "b"})));

    assertInstanceOf(ScalarOpcUaNode.class, node);
  }

  @Test
  void extensionObjectValueIsStructured() {
    var value = ExtensionObject.of(ByteString.of(new byte[] {1}), new NodeId(3, Unsigned.uint(5001)));

    var node = OpcUaNodeFactory.createOpcUaNode(nodeInfo(VENDOR_STRING_TYPE), good(new Variant(value)));

    assertInstanceOf(StructuredOpcUaNode.class, node);
  }

  @Test
  void extensionObjectArrayIsStructured() {
    var value = ExtensionObject.of(ByteString.of(new byte[] {1}), new NodeId(3, Unsigned.uint(5001)));

    var node = OpcUaNodeFactory.createOpcUaNode(
        nodeInfo(NodeIds.Structure), good(new Variant(new ExtensionObject[] {value})));

    assertInstanceOf(StructuredOpcUaNode.class, node);
  }

  @Test
  void vendorStringSubtypeWithoutGoodValueIsScalar() {
    var bad = new DataValue(Variant.NULL_VALUE, new StatusCode(StatusCodes.Bad_WaitingForInitialData), null);

    assertInstanceOf(ScalarOpcUaNode.class, OpcUaNodeFactory.createOpcUaNode(nodeInfo(VENDOR_STRING_TYPE), bad));
    assertInstanceOf(ScalarOpcUaNode.class, OpcUaNodeFactory.createOpcUaNode(nodeInfo(VENDOR_STRING_TYPE), null));
  }

  @Test
  void structureDataTypeWithoutGoodValueIsStructured() {
    var node = OpcUaNodeFactory.createOpcUaNode(nodeInfo(NodeIds.Structure), null);

    assertInstanceOf(StructuredOpcUaNode.class, node);
  }

  private static DataValue good(Variant value) {
    return new DataValue(value, StatusCode.GOOD, null);
  }

  private static BasicVariableNodeInfo nodeInfo(NodeId dataType) {
    var variableNode = mock(VariableNode.class);
    when(variableNode.getDataType()).thenReturn(dataType);
    var nodeInfo = mock(BasicVariableNodeInfo.class);
    when(nodeInfo.getNode()).thenReturn(variableNode);
    return nodeInfo;
  }
}
