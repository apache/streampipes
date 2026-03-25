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

package org.apache.streampipes.connect.transformer.js;

import org.apache.streampipes.connect.transformer.api.exception.ScriptExecutionException;
import org.apache.streampipes.connect.transformer.api.utils.TransformationEngineConversionUtils;

import org.graalvm.polyglot.Value;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PolyglotTypeConverter {

  private PolyglotTypeConverter() {
  }

  public static Map<String, Object> toEventMap(Object input) throws ScriptExecutionException {
    Object javaObject = toJavaValue(input);
    if (javaObject == null) {
      return null;
    }
    if (javaObject instanceof Map<?, ?> map) {
      return TransformationEngineConversionUtils.copyToStringKeyMap(map);
    }
    throw new ScriptExecutionException(
        "Expected a map-compatible event but got " + javaObject.getClass().getName());
  }

  public static Object toJavaValue(Object input) {
    if (input == null) {
      return null;
    }
    if (input instanceof Value value) {
      return fromValue(value);
    }
    if (input instanceof String || input instanceof Number || input instanceof Boolean) {
      return input;
    }
    if (input instanceof Map<?, ?> map) {
      return TransformationEngineConversionUtils.copyToStringKeyMap(map);
    }
    if (input instanceof List<?> list) {
      List<Object> result = new ArrayList<>(list.size());
      for (Object element : list) {
        result.add(toJavaValue(element));
      }
      return result;
    }
    try {
      Value value = Value.asValue(input);
      if (!value.isHostObject() || value.hasHashEntries() || value.hasArrayElements() || value.hasMembers()) {
        return fromValue(value);
      }
    } catch (IllegalArgumentException ignored) {
      // Fall back to the original object if it cannot be represented as a polyglot value.
    }
    return input;
  }

  private static Object fromValue(Value value) {
    if (value.isNull()) {
      return null;
    }
    if (value.hasHashEntries()) {
      Map<String, Object> result = new LinkedHashMap<>();
      Value iterator = value.getHashEntriesIterator();
      while (iterator.hasIteratorNextElement()) {
        Value entry = iterator.getIteratorNextElement();
        result.put(String.valueOf(fromValue(entry.getArrayElement(0))), fromValue(entry.getArrayElement(1)));
      }
      return result;
    }
    if (value.hasArrayElements()) {
      List<Object> result = new ArrayList<>();
      for (long index = 0; index < value.getArraySize(); index++) {
        result.add(fromValue(value.getArrayElement(index)));
      }
      return result;
    }
    if (value.hasMembers()) {
      Map<String, Object> result = new LinkedHashMap<>();
      for (String key : value.getMemberKeys()) {
        result.put(key, fromValue(value.getMember(key)));
      }
      return result;
    }
    if (value.isBoolean()) {
      return value.asBoolean();
    }
    if (value.isString()) {
      return value.asString();
    }
    if (value.fitsInInt()) {
      return value.asInt();
    }
    if (value.fitsInLong()) {
      return value.asLong();
    }
    if (value.fitsInDouble()) {
      return value.asDouble();
    }
    if (value.isHostObject()) {
      return value.asHostObject();
    }
    return value.as(Object.class);
  }
}
