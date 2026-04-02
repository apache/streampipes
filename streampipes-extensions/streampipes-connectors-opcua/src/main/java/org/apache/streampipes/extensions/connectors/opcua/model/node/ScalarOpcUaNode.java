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
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Matrix;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.XmlElement;

import java.lang.reflect.Array;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScalarOpcUaNode implements OpcUaNode {

  private final BasicVariableNodeInfo nodeInfo;

  public ScalarOpcUaNode(BasicVariableNodeInfo nodeInfo) {
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
    event.put(nodeName, extractValue(variant));
  }

  @Override
  public void addToEventPreview(OpcUaClient client,
                                Map<String, Object> eventPreview,
                                Map<String, FieldStatusInfo> fieldStatusInfos,
                                Variant variant,
                                FieldStatusInfo fieldStatusInfo) {
    if (fieldStatusInfo.getFieldStatus() == FieldStatus.GOOD) {
      eventPreview.put(nodeInfo().getBaseNodeName(), extractValue(variant));
    }
    fieldStatusInfos.put(nodeInfo().getBaseNodeName(), fieldStatusInfo);
  }

  private Object extractValue(Variant variant) {
    return extractRawValue(variant.getValue());
  }

  private Object extractRawValue(Object rawValue) {
    if (rawValue instanceof ByteString) {
      // encode ByteString to base64 string
      return Base64.getEncoder().encodeToString(((ByteString) rawValue).bytes());
    } else if (isByteStringArray(rawValue)) {
      return extractByteStringArray(rawValue);
    } else if (rawValue instanceof Matrix) {
      return extractMatrix((Matrix) rawValue);
    } else if (rawValue instanceof DataValue) {
      return extractDataValue((DataValue) rawValue);
    } else if (rawValue instanceof ExpandedNodeId) {
      return extractExpandedNodeId((ExpandedNodeId) rawValue);
    } else if (rawValue instanceof DateTime) {
      // convert DateTime to UTC timestamp in ms
      return ((DateTime) rawValue).getJavaTime();
    } else if (rawValue instanceof XmlElement) {
      return ((XmlElement) rawValue).getFragment();
    }

    return rawValue;
  }

  private boolean isByteStringArray(Object value) {
    return value != null
        && value.getClass().isArray()
        && ByteString.class.equals(value.getClass().getComponentType());
  }

  private Object[] extractByteStringArray(Object byteStringArray) {
    var length = Array.getLength(byteStringArray);
    var extractedValues = new Object[length];

    for (int i = 0; i < length; i++) {
      var value = (ByteString) Array.get(byteStringArray, i);
      extractedValues[i] = extractByteStringValue(value);
    }

    return extractedValues;
  }

  private String extractByteStringValue(Object value) {
    return Base64.getEncoder().encodeToString(((ByteString) value).bytes());
  }

  private Map<String, Object> extractMatrix(Matrix matrix) {
    var extractedMatrix = new LinkedHashMap<String, Object>();
    extractedMatrix.put("elements", arrayToList(matrix.getElements()));
    extractedMatrix.put("dimensions", intArrayToList(matrix.getDimensions()));
    return extractedMatrix;
  }

  private List<Object> arrayToList(Object array) {
    var length = Array.getLength(array);
    var values = new Object[length];

    for (int i = 0; i < length; i++) {
      values[i] = extractRawValue(Array.get(array, i));
    }

    return List.of(values);
  }

  private List<Integer> intArrayToList(int[] values) {
    var dimensions = new Integer[values.length];
    for (int i = 0; i < values.length; i++) {
      dimensions[i] = values[i];
    }
    return List.of(dimensions);
  }

  private Map<String, Object> extractDataValue(DataValue dataValue) {
    var extractedDataValue = new LinkedHashMap<String, Object>();
    extractedDataValue.put("value", dataValue.getValue() != null ? extractValue(dataValue.getValue()) : null);
    extractedDataValue.put("statusCode", formatStatusCode(dataValue.getStatusCode()));
    extractedDataValue.put("sourceTimestamp", formatDateTime(dataValue.getSourceTime()));
    extractedDataValue.put("serverTimestamp", formatDateTime(dataValue.getServerTime()));
    return extractedDataValue;
  }

  private String formatStatusCode(StatusCode statusCode) {
    if (statusCode == null) {
      return null;
    }

    var value = statusCode.getValue();
    var statusName = StatusCodes.lookup(value)
        .filter(names -> names.length > 0)
        .map(names -> names[0])
        .orElse("Unknown");

    return statusName + " (" + Long.toUnsignedString(value) + ")";
  }

  private Map<String, Object> extractExpandedNodeId(ExpandedNodeId expandedNodeId) {
    var extractedNodeId = new LinkedHashMap<String, Object>();
    extractedNodeId.put("identifier", expandedNodeId.getIdentifier());
    extractedNodeId.put(
        "namespaceIndex",
        expandedNodeId.getNamespaceIndex() != null ? expandedNodeId.getNamespaceIndex().intValue() : null
    );
    extractedNodeId.put("type", expandedNodeId.getType() != null ? expandedNodeId.getType().name() : null);
    extractedNodeId.put("namespaceUri", expandedNodeId.getNamespaceUri());
    extractedNodeId.put(
        "serverIndex",
        expandedNodeId.getServerIndex() != null ? expandedNodeId.getServerIndex().longValue() : null
    );
    return extractedNodeId;
  }

  private String formatDateTime(DateTime dateTime) {
    if (dateTime == null) {
      return null;
    }

    return dateTime.toIso8601String();
  }
}
