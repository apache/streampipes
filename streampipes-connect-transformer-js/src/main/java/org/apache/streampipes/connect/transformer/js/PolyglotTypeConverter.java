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
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Normalizes GraalVM polyglot values into plain Java types used by the StreamPipes transformation runtime.
 *
 * <p>The JavaScript transformer boundary accepts values that may originate from GraalJS {@link Value} instances,
 * proxy objects, Java collections, or regular Java primitives. This utility unwraps that mixed input into stable
 * Java representations such as {@link Map}, {@link List}, {@link String}, {@link Number}, and {@link Boolean} so the
 * rest of the transformation pipeline can work with predictable types.
 *
 * <p>A common use case is script output handling. When a script calls {@code out.collect(...)} with a JavaScript
 * object, the collector uses this converter to turn that object into a {@code Map<String, Object>} event structure
 * before handing it back to the StreamPipes engine. Nested arrays, objects, and host values are converted
 * recursively, which keeps the event payload compatible with the existing transformation APIs.
 */
public final class PolyglotTypeConverter {

  private PolyglotTypeConverter() {
  }

  /**
   * Converts a script-produced event into the Java event representation expected by the output collector.
   *
   * @throws ScriptExecutionException if the provided value cannot be represented as a map-like event
   */
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

  /**
   * Recursively unwraps a polyglot input value into plain Java objects.
   */
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
    if (input.getClass().isArray()) {
      return getArrayValue(input);
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

  @NonNull
  private static Object getArrayValue(Object input) {
    int length = Array.getLength(input);
    List<Object> result = new ArrayList<>(length);
    for (int i = 0; i < length; i++) {
      result.add(toJavaValue(Array.get(input, i)));
    }
    return result;
  }

  private static Object fromValue(Value value) {
    if (value.isNull()) {
      return null;
    }
    if (value.isHostObject()) {
      Object hostObject = value.asHostObject();
      if (hostObject != null && hostObject.getClass().isArray()) {
        return getArrayValue(hostObject);
      }
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
