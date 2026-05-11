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

import org.eclipse.milo.opcua.stack.core.types.UaEnumeratedType;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.Matrix;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.ULong;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class OpcUaValueNormalizationUtils {

  private OpcUaValueNormalizationUtils() {
  }

  public static Optional<Object> tryNormalizeCommonValue(Object value,
                                                         Function<Object, Object> recursiveNormalizer) {
    if (value instanceof ByteString byteString) {
      var bytes = byteString.bytes();
      return Optional.ofNullable(bytes == null ? null : Base64.getEncoder().encodeToString(bytes));
    }

    if (value instanceof DateTime dateTime) {
      return Optional.of(dateTime.getJavaTime());
    }

    if (value instanceof NodeId nodeId) {
      return Optional.of(nodeId.toParseableString());
    }

    if (value instanceof ExpandedNodeId expandedNodeId) {
      return Optional.of(expandedNodeId.toParseableString());
    }

    if (value instanceof QualifiedName qualifiedName) {
      return Optional.of(qualifiedName.toParseableString());
    }

    if (value instanceof LocalizedText localizedText) {
      return Optional.ofNullable(localizedText.getText());
    }

    if (value instanceof StatusCode statusCode) {
      return Optional.of(statusCode.getValue());
    }

    if (value instanceof UaEnumeratedType enumeratedType) {
      return Optional.of(
          enumeratedType.getName() != null ? enumeratedType.getName() : enumeratedType.getValue()
      );
    }

    if (value instanceof Matrix matrix) {
      return Optional.of(matrixToMap(matrix, recursiveNormalizer));
    }

    value = normalizeUnsignedNumber(value);

    if (isScalar(value)) {
      return Optional.of(value);
    }

    if (value instanceof Map<?, ?> mapValue) {
      var normalized = new LinkedHashMap<String, Object>();
      mapValue.forEach((key, mapEntryValue) ->
          normalized.put(String.valueOf(key), recursiveNormalizer.apply(mapEntryValue)));
      return Optional.of(normalized);
    }

    if (value instanceof Iterable<?> iterable) {
      var normalized = new ArrayList<>();
      iterable.forEach(entry -> normalized.add(recursiveNormalizer.apply(entry)));
      return Optional.of(normalized);
    }

    if (value.getClass().isArray()) {
      return Optional.of(arrayToList(value, recursiveNormalizer));
    }

    if (value instanceof Enum<?> enumValue) {
      return Optional.of(enumValue.name());
    }

    return Optional.empty();
  }

  private static Map<String, Object> matrixToMap(Matrix matrix,
                                                 Function<Object, Object> recursiveNormalizer) {
    var normalized = new LinkedHashMap<String, Object>();
    normalized.put("elements", arrayToList(matrix.getElements(), recursiveNormalizer));
    normalized.put("dimensions", intArrayToList(matrix.getDimensions()));
    return normalized;
  }

  private static List<Object> arrayToList(Object arrayValue,
                                          Function<Object, Object> recursiveNormalizer) {
    var length = Array.getLength(arrayValue);
    var values = new ArrayList<>(length);
    for (int i = 0; i < length; i++) {
      values.add(recursiveNormalizer.apply(Array.get(arrayValue, i)));
    }
    return values;
  }

  private static List<Integer> intArrayToList(int[] values) {
    var normalized = new ArrayList<Integer>(values.length);
    for (var value : values) {
      normalized.add(value);
    }
    return normalized;
  }

  private static boolean isScalar(Object value) {
    return value instanceof String
        || value instanceof Number
        || value instanceof Boolean
        || value instanceof Character;
  }

  public static Object normalizeUnsignedNumber(Object value) {
    if (value instanceof UByte uByte) {
      return uByte.intValue();
    }

    if (value instanceof UShort uShort) {
      return uShort.intValue();
    }

    if (value instanceof UInteger uInteger) {
      return uInteger.longValue();
    }

    if (value instanceof ULong uLong) {
      return uLong.longValue();
    }

    return value;
  }
}
