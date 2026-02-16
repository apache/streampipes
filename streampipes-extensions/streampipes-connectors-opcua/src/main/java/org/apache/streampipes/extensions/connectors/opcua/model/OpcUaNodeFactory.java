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
import org.apache.streampipes.extensions.connectors.opcua.model.node.ExtensionObjectOpcUaNode;
import org.apache.streampipes.extensions.connectors.opcua.model.node.OpcUaNode;
import org.apache.streampipes.extensions.connectors.opcua.model.node.PrimitiveOpcUaNode;

import org.eclipse.milo.opcua.stack.core.BuiltinDataType;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Objects;

public class OpcUaNodeFactory {

  public static OpcUaNode createOpcUaNode(
      BasicVariableNodeInfo nodeInfo,
      DataValue dataValue
  ) {
    Boolean byValue = isExtensionByValue(dataValue);
    if (byValue != null) {
      return byValue
          ? new ExtensionObjectOpcUaNode(nodeInfo)
          : new PrimitiveOpcUaNode(nodeInfo);
    }

    return isExtensionByDataType(nodeInfo)
        ? new ExtensionObjectOpcUaNode(nodeInfo)
        : new PrimitiveOpcUaNode(nodeInfo);
  }

  /**
   * @return TRUE  -> value is (or contains) ExtensionObject
   *         FALSE -> value is present and not ExtensionObject
   *         NULL  -> cannot decide from value (null DataValue, bad StatusCode, no Variant, etc.)
   */
  public static Boolean isExtensionByValue(DataValue dv) {
    if (dv == null) {
      return null;
    }

    StatusCode sc = dv.getStatusCode();
    if (sc == null || !sc.isGood()) {
      return null;
    }

    if (dv.getValue() == null) {
      return null;
    }

    Object v = dv.getValue().getValue();
    if (v == null) {
      return Boolean.FALSE;
    }

    if (v instanceof ExtensionObject) {
      return Boolean.TRUE;
    }

    // Handle arrays of any kind (Object[] or primitive arrays)
    Class<?> c = v.getClass();
    if (c.isArray()) {
      int len = Array.getLength(v);
      for (int i = 0; i < len; i++) {
        Object el = Array.get(v, i); // works for primitive arrays too (boxed)
        if (el instanceof ExtensionObject) {
          return Boolean.TRUE;
        }
      }
      return Boolean.FALSE;
    }

    return Boolean.FALSE;
  }

  /**
   * Conservative fallback based on declared DataType only.
   * Treat only ExtensionObject itself as "extension" here.
   *
   * Why so conservative? Because abstract standard types like Integer/Number
   * are NOT builtins but are still "primitive-ish" and should not be treated
   * as custom/extension.
   */
  private static boolean isExtensionByDataType(BasicVariableNodeInfo nodeInfo) {
    NodeId dt = nodeInfo.getNode().getDataType();
    return Objects.equals(dt, BuiltinDataType.ExtensionObject.getNodeId());
  }
}

