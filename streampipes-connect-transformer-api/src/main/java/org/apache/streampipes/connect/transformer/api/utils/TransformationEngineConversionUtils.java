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

package org.apache.streampipes.connect.transformer.api.utils;

import org.apache.streampipes.connect.transformer.api.OutputCollector;
import org.apache.streampipes.connect.transformer.api.exception.ScriptExecutionException;

import java.util.HashMap;
import java.util.Map;

public class TransformationEngineConversionUtils {

  public static Map<String, Object> ensureMap(Object result, String language) throws ScriptExecutionException {
    if (result == null) {
      return Map.of();
    }

    if (result instanceof Map<?, ?> rawMap) {
      return copyToStringKeyMap(rawMap);
    }

    throw new ScriptExecutionException(
        "Template in " + language + " must return a Map<String, Object>, but returned: "
            + result.getClass().getName());
  }

  public static Map<String, Object> copyToStringKeyMap(Map<?, ?> rawMap) {
    Map<String, Object> result = new HashMap<>();
    rawMap.forEach((k, v) -> result.put(String.valueOf(k), v));
    return result;
  }

  public static OutputCollector<Object> convertingCollector(OutputCollector<Map<String, Object>> delegate,
                                                            String language) {
    return eventObj -> {
      Map<String, Object> map = ensureMap(
          eventObj,
          language
      );
      delegate.collect(map);
    };
  }
}
