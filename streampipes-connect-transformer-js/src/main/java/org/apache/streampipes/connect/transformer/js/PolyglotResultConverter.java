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

import org.apache.streampipes.connect.transformer.api.OutputCollector;
import org.apache.streampipes.connect.transformer.api.exception.ScriptExecutionException;
import org.apache.streampipes.connect.transformer.api.utils.TransformationEngineConversionUtils;

import org.graalvm.polyglot.Value;

import java.util.HashMap;
import java.util.Map;

public class PolyglotResultConverter {

  public static Map<String, Object> ensureMap(Object result, String language) throws ScriptExecutionException {

    if (result == null) {
      return Map.of();
    }

    if (result instanceof Map<?, ?> rawMap) {
      return TransformationEngineConversionUtils.copyToStringKeyMap(rawMap);
    }

    if (result instanceof Value value) {
      return convertValue(value, language);
    }

    throw new ScriptExecutionException(
        "Template in " + language + " must return a Map<String, Object>, but returned: "
            + result.getClass().getName());
  }

  private static Map<String, Object> convertValue(Value value, String language)
      throws ScriptExecutionException {
    if (value.hasHashEntries()) {
      try {
        return TransformationEngineConversionUtils.copyToStringKeyMap(value.as(Map.class));
      } catch (Exception e) {
        throw new ScriptExecutionException(
            "Template in " + language + " returned an unreadable map-like value", e);
      }
    }

    if (value.hasMembers()) {
      Map<String, Object> result = new HashMap<>();
      for (String key : value.getMemberKeys()) {
        result.put(key, value.getMember(key).as(Object.class));
      }
      return result;
    }

    throw new ScriptExecutionException(
        "Template in " + language + " returned a non-object value: " + value.toString());
  }

  public static OutputCollector<Object> convertingCollector(OutputCollector<Map<String, Object>> delegate, String language) {
    return obj -> {
      Map<String,Object> map;
      if (obj instanceof Map<?, ?> m) {
        //noinspection unchecked
        map = (Map<String,Object>) m;
      } else if (obj instanceof Value v) {
        map = PolyglotResultConverter.ensureMap(v, language);
      } else {
        throw new IllegalArgumentException("Collected event must be an object/map");
      }
      delegate.collect(map);
    };
  }
}
