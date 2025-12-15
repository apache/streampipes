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

package org.apache.streampipes.extensions.connectors.opcua.utils;

import org.apache.streampipes.sdk.utils.Datatypes;

import org.eclipse.milo.opcua.sdk.core.nodes.VariableNode;
import org.eclipse.milo.opcua.stack.core.BuiltinDataType;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;

import java.util.Objects;

public class OpcUaTypes {

  /**
   * Maps OPC UA data types to internal StreamPipes data types
   *
   * @param o data type id as UInteger
   * @return StreamPipes internal data type
   */
  public static Datatypes getType(UInteger o) {
    if (UInteger.valueOf(4).equals(o)
        | UInteger.valueOf(5).equals(o)
        | UInteger.valueOf(6).equals(o)
        | UInteger.valueOf(7).equals(o)
        | UInteger.valueOf(8).equals(o)
        | UInteger.valueOf(9).equals(o)
        | UInteger.valueOf(27).equals(o)) {
      return Datatypes.Integer;
    } else if (UInteger.valueOf(8).equals(o)) {
      return Datatypes.Long;
    } else if (UInteger.valueOf(11).equals(o)) {
      return Datatypes.Double;
    } else if (UInteger.valueOf(10).equals(o) | UInteger.valueOf(26).equals(o) | UInteger.valueOf(50).equals(o)) {
      return Datatypes.Float;
    } else if (UInteger.valueOf(1).equals(o)) {
      return Datatypes.Boolean;
    } else if (UInteger.valueOf(12).equals(o)) {
      return Datatypes.String;
    } else if (UInteger.valueOf(13).equals(o)) {
      return Datatypes.Long;
    }

    return Datatypes.String;
  }

  public static Datatypes getTypeFromValue(Object value) {
    if (value instanceof Boolean) {
      return Datatypes.Boolean;
    } else if (value instanceof Integer) {
      return Datatypes.Integer;
    } else if (value instanceof Long) {
      return Datatypes.Long;
    } else if (value instanceof Float) {
      return Datatypes.Float;
    } else if (value instanceof Double) {
      return Datatypes.Double;
    } else {
      return Datatypes.String;
    }
  }

  /**
   * Determines if the node is an extension data type or a custom data type
   *
   * @param node a data variable node
   * @return true if the node is an ExtensionObject or custom data type
   */
  public static boolean isExtensionOrCustom(VariableNode node) {
    return !BuiltinDataType.isBuiltin(node.getDataType())
        || Objects.equals(node.getDataType(), BuiltinDataType.ExtensionObject.getNodeId());
  }

}

