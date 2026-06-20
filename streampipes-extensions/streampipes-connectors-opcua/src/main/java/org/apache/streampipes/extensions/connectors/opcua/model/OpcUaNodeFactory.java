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
import org.apache.streampipes.extensions.connectors.opcua.model.node.OpcUaNode;
import org.apache.streampipes.extensions.connectors.opcua.model.node.ScalarOpcUaNode;
import org.apache.streampipes.extensions.connectors.opcua.model.node.StructuredOpcUaNode;

import org.eclipse.milo.opcua.stack.core.NodeIds;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;

import java.lang.reflect.Array;
import java.util.Objects;

public class OpcUaNodeFactory {

  public static OpcUaNode createOpcUaNode(
      BasicVariableNodeInfo nodeInfo,
      DataValue dataValue
  ) {
    var hasVariant = hasVariant(dataValue);
    if (hasVariant) {
      var byValue = isExtensionByValue(dataValue);
        return byValue
            ? new StructuredOpcUaNode(nodeInfo)
            : new ScalarOpcUaNode(nodeInfo);
    }

    return isExtensionByDataType(nodeInfo)
        ? new StructuredOpcUaNode(nodeInfo)
        : new ScalarOpcUaNode(nodeInfo);
  }

  private static boolean hasVariant(DataValue dataValue) {
    if (dataValue == null) {
      return false;
    }

    StatusCode sc = dataValue.getStatusCode();
    if (sc == null || !sc.isGood()) {
      return false;
    }

    return dataValue.getValue() != null;
  }

  /**
   * @return TRUE  -> value is (or contains) ExtensionObject
   *         FALSE -> value is present and not ExtensionObject
   */
  private static boolean isExtensionByValue(DataValue dv) {
    Object v = dv.getValue().getValue();
    if (v == null) {
      return false;
    }

    if (v instanceof ExtensionObject) {
      return true;
    }

    // Handle arrays of any kind (Object[] or primitive arrays)
    Class<?> c = v.getClass();
    if (c.isArray()) {
      int len = Array.getLength(v);
      for (int i = 0; i < len; i++) {
        Object el = Array.get(v, i);
        if (el instanceof ExtensionObject) {
          return true;
        }
      }
      return false;
    }

    return false;
  }

  /**
   * Conservative fallback based on declared DataType only.
   * Treat only Structure/ExtensionObject data type itself as "extension" here.
   *
   * Why so conservative? Because abstract standard types like Integer/Number
   * are NOT builtins but are still "primitive-ish" and should not be treated
   * as custom/extension.
   */
  private static boolean isExtensionByDataType(BasicVariableNodeInfo nodeInfo) {
    NodeId dt = nodeInfo.getNode().getDataType();
    return Objects.equals(dt, NodeIds.Structure);
  }
}
