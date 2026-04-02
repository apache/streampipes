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

import org.apache.streampipes.model.connect.guess.FieldStatus;
import org.apache.streampipes.model.connect.guess.FieldStatusInfo;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.core.dtd.BsdStructWrapper;
import org.eclipse.milo.opcua.sdk.core.dtd.generic.Struct;
import org.eclipse.milo.opcua.sdk.core.types.DynamicEnumType;
import org.eclipse.milo.opcua.sdk.core.types.DynamicOptionSetType;
import org.eclipse.milo.opcua.sdk.core.types.DynamicStructType;
import org.eclipse.milo.opcua.sdk.core.types.DynamicUnionType;
import org.eclipse.milo.opcua.stack.core.types.UaEnumeratedType;
import org.eclipse.milo.opcua.stack.core.types.UaStructuredType;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.Matrix;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StructuredOpcUaNode implements OpcUaNode {

  private static final Logger LOG = LoggerFactory.getLogger(StructuredOpcUaNode.class);
  private static final int MAX_DEPTH = 20;

  private final BasicVariableNodeInfo nodeInfo;

  public StructuredOpcUaNode(BasicVariableNodeInfo nodeInfo) {
    this.nodeInfo = nodeInfo;
  }

  @Override
  public BasicVariableNodeInfo nodeInfo() {
    return nodeInfo;
  }

  @Override
  public int getNumberOfEventProperties(OpcUaClient client) {
    return 1;
  }

  @Override
  public void addToEvent(OpcUaClient client,
                         Map<String, Object> event,
                         Variant variant) {
    var nodeName = nodeInfo.getDesiredName("");
    event.put(nodeName, normalizeVariant(client, variant));
  }

  @Override
  public void addToEventPreview(OpcUaClient client,
                                Map<String, Object> eventPreview,
                                Map<String, FieldStatusInfo> fieldStatusInfos,
                                Variant variant,
                                FieldStatusInfo fieldStatusInfo) {
    var nodeName = nodeInfo().getBaseNodeName();
    if (fieldStatusInfo.getFieldStatus() == FieldStatus.GOOD) {
      eventPreview.put(nodeName, normalizeVariant(client, variant));
    }
    fieldStatusInfos.put(nodeName, fieldStatusInfo);
  }

  private Object normalizeVariant(OpcUaClient client,
                                  Variant variant) {
    if (variant == null) {
      return null;
    }
    return normalizeValue(client, variant.getValue(), 0);
  }

  private Object normalizeValue(OpcUaClient client,
                                Object value,
                                int depth) {
    if (value == null) {
      return null;
    }
    if (depth > MAX_DEPTH) {
      return String.valueOf(value);
    }

    if (value instanceof ExtensionObject extensionObject) {
      return decodeExtensionObject(client, extensionObject, depth + 1);
    }

    if (value instanceof Struct struct) {
      return structToMap(client, struct, depth + 1);
    }

    if (value instanceof BsdStructWrapper<?> wrapper) {
      return normalizeValue(client, wrapper.object(), depth + 1);
    }

    if (value instanceof DynamicStructType dynamicStructType) {
      return dynamicStructToMap(client, dynamicStructType, depth + 1);
    }

    if (value instanceof DynamicUnionType dynamicUnionType) {
      return dynamicUnionToMap(client, dynamicUnionType, depth + 1);
    }

    if (value instanceof DynamicOptionSetType dynamicOptionSetType) {
      return dynamicOptionSetToMap(client, dynamicOptionSetType, depth + 1);
    }

    if (value instanceof DynamicEnumType dynamicEnumType) {
      return dynamicEnumToValue(dynamicEnumType);
    }

    if (value instanceof ByteString byteString) {
      var bytes = byteString.bytes();
      return bytes == null ? null : Base64.getEncoder().encodeToString(bytes);
    }

    if (value instanceof Matrix matrix) {
      return matrixToMap(client, matrix, depth + 1);
    }

    if (value instanceof DateTime dateTime) {
      return dateTime.getJavaTime();
    }

    if (value instanceof NodeId nodeId) {
      return nodeId.toParseableString();
    }

    if (value instanceof ExpandedNodeId expandedNodeId) {
      return expandedNodeId.toParseableString();
    }

    if (value instanceof QualifiedName qualifiedName) {
      return qualifiedName.toParseableString();
    }

    if (value instanceof LocalizedText localizedText) {
      return localizedText.getText();
    }

    if (value instanceof StatusCode statusCode) {
      return statusCode.getValue();
    }

    if (value instanceof UaEnumeratedType uaEnumeratedType) {
      return uaEnumeratedType.getName() != null ? uaEnumeratedType.getName() : uaEnumeratedType.getValue();
    }

    if (isScalar(value)) {
      return value;
    }

    if (value instanceof Map<?, ?> mapValue) {
      return mapToMap(client, mapValue, depth + 1);
    }

    if (value instanceof Iterable<?> iterable) {
      var listValue = new ArrayList<>();
      for (Object element : iterable) {
        listValue.add(normalizeValue(client, element, depth + 1));
      }
      return listValue;
    }

    if (value.getClass().isArray()) {
      int length = Array.getLength(value);
      List<Object> arrayValue = new ArrayList<>(length);
      for (int i = 0; i < length; i++) {
        arrayValue.add(normalizeValue(client, Array.get(value, i), depth + 1));
      }
      return arrayValue;
    }

    if (value instanceof UaStructuredType) {
      return objectToMap(client, value, depth + 1);
    }

    if (value instanceof Enum<?> enumValue) {
      return enumValue.name();
    }

    return objectToMap(client, value, depth + 1);
  }

  private Map<String, Object> structToMap(OpcUaClient client,
                                          Struct struct,
                                          int depth) {
    var result = new LinkedHashMap<String, Object>();
    struct.getMembers().forEach((key, member) ->
        result.put(key, normalizeValue(client, member.getValue(), depth + 1)));
    return result;
  }

  private Map<String, Object> mapToMap(OpcUaClient client,
                                       Map<?, ?> mapValue,
                                       int depth) {
    var result = new LinkedHashMap<String, Object>();
    mapValue.forEach((key, entryValue) ->
        result.put(String.valueOf(key), normalizeValue(client, entryValue, depth + 1)));
    return result;
  }

  private Map<String, Object> dynamicStructToMap(OpcUaClient client,
                                                 DynamicStructType dynamicStructType,
                                                 int depth) {
    var result = new LinkedHashMap<String, Object>();
    dynamicStructType.getMembers().forEach((key, memberValue) ->
        result.put(key, normalizeValue(client, memberValue, depth + 1)));
    return result;
  }

  private Map<String, Object> dynamicUnionToMap(OpcUaClient client,
                                                DynamicUnionType dynamicUnionType,
                                                int depth) {
    var result = new LinkedHashMap<String, Object>();
    dynamicUnionType.getValue().ifPresent(unionValue ->
        result.put(unionValue.fieldName(), normalizeValue(client, unionValue.fieldValue(), depth + 1)));
    return result;
  }

  private Map<String, Object> dynamicOptionSetToMap(OpcUaClient client,
                                                    DynamicOptionSetType dynamicOptionSetType,
                                                    int depth) {
    var result = new LinkedHashMap<String, Object>();
    result.put("value", normalizeValue(client, dynamicOptionSetType.getValue(), depth + 1));
    result.put("validBits", normalizeValue(client, dynamicOptionSetType.getValidBits(), depth + 1));
    return result;
  }

  private Object dynamicEnumToValue(DynamicEnumType dynamicEnumType) {
    var enumName = dynamicEnumType.getName();
    return enumName != null ? enumName : dynamicEnumType.getValue();
  }

  private Map<String, Object> matrixToMap(OpcUaClient client,
                                          Matrix matrix,
                                          int depth) {
    var result = new LinkedHashMap<String, Object>();
    result.put("elements", arrayToList(client, matrix.getElements(), depth + 1));
    result.put("dimensions", intArrayToList(matrix.getDimensions()));
    return result;
  }

  private List<Object> arrayToList(OpcUaClient client,
                                   Object arrayValue,
                                   int depth) {
    int length = Array.getLength(arrayValue);
    var values = new ArrayList<>(length);
    for (int i = 0; i < length; i++) {
      values.add(normalizeValue(client, Array.get(arrayValue, i), depth + 1));
    }
    return values;
  }

  private List<Integer> intArrayToList(int[] values) {
    var dimensions = new ArrayList<Integer>(values.length);
    for (var value : values) {
      dimensions.add(value);
    }
    return dimensions;
  }

  private Object decodeExtensionObject(OpcUaClient client,
                                       ExtensionObject extensionObject,
                                       int depth) {
    try {
      var decoded = extensionObject.decode(client.getDynamicEncodingContext());
      return normalizeValue(client, decoded, depth + 1);
    } catch (Exception e) {
      return undecodedExtensionObjectToMap(client, extensionObject, e.getMessage(), depth + 1);
    }
  }

  private Map<String, Object> undecodedExtensionObjectToMap(OpcUaClient client,
                                                            ExtensionObject extensionObject,
                                                            String error,
                                                            int depth) {
    var result = new LinkedHashMap<String, Object>();
    var encodingId = extensionObject.getEncodingOrTypeId() != null
        ? extensionObject.getEncodingOrTypeId().toParseableString()
        : "null";
    LOG.warn("Could not decode ExtensionObject with encodingId {}: {}", encodingId, error);

    result.put("_decodeError", error);
    result.put("_encodingId", encodingId);
    var body = extensionObject.getBody();
    result.put("_bodyType", body == null ? "null" : body.getClass().getSimpleName());
    result.put("_rawBody", normalizeValue(client, body, depth + 1));
    return result;
  }

  private Map<String, Object> objectToMap(OpcUaClient client,
                                          Object value,
                                          int depth) {
    var result = new LinkedHashMap<String, Object>();
    result.put("_javaType", value.getClass().getName());

    var hasFields = false;
    Class<?> currentClass = value.getClass();
    while (currentClass != null && currentClass != Object.class) {
      for (var field : currentClass.getDeclaredFields()) {
        if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic() || result.containsKey(field.getName())) {
          continue;
        }

        try {
          field.setAccessible(true);
          var fieldValue = field.get(value);
          result.put(field.getName(), normalizeValue(client, fieldValue, depth + 1));
          hasFields = true;
        } catch (Exception e) {
          result.put(field.getName(), "unavailable");
        }
      }
      currentClass = currentClass.getSuperclass();
    }

    if (!hasFields) {
      result.put("_value", String.valueOf(value));
    }

    return result;
  }

  private boolean isScalar(Object value) {
    return value instanceof String
        || value instanceof Number
        || value instanceof Boolean
        || value instanceof Character;
  }
}
