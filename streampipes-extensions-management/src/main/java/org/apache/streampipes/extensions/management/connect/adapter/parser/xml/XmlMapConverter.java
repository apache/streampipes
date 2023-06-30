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
package org.apache.streampipes.extensions.management.connect.adapter.parser.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlMapConverter {

  private Map<String, Object> inMap;

  public XmlMapConverter(Map<String, Object> inMap) {
    this.inMap = inMap;
  }

  public Map<String, Object> convert() {
    convert(inMap);
    this.inMap = replaceKeys(inMap);
    return this.inMap;
  }

  @SuppressWarnings({"unchecked"})
  private void convert(Map<String, Object> map) {
    for (String key : map.keySet()) {
      Object value = map.get(key);
      if (value instanceof Map) {
        convert((Map<String, Object>) map.get(key));
      } else if (value instanceof String) {
        String stringValue = String.valueOf(value);

        if (isBoolean(stringValue)) {
          map.put(key, Boolean.parseBoolean(stringValue));
        } else if (isLong(stringValue)) {
          map.put(key, Long.parseLong(stringValue));
        } else if (isInteger(stringValue)) {
          map.put(key, Integer.parseInt(stringValue));
        } else if (isFloat(stringValue)) {
          map.put(key, Float.parseFloat(stringValue));
        }
      } else if (value instanceof List) {
        ((List) value).forEach(item -> convert((Map<String, Object>) item));
      }
    }
  }

  @SuppressWarnings({"unchecked"})
  private Map<String, Object> replaceKeys(Map<String, Object> map) {
    Map<String, Object> outMap = new HashMap<>();
    for (String key : map.keySet()) {
      String newKey = key;
      if (key.startsWith("-")) {
        newKey = key.substring(1);
      }
      if (!key.equals("-self-closing")) {
        outMap.put(newKey, makeObject(map.get(key)));
      }
    }
    return outMap;
  }

  @SuppressWarnings({"unchecked"})
  private Object makeObject(Object value) {
    if (value instanceof List) {
      List<Map<String, Object>> values = new ArrayList<>();
      ((List) value).forEach(item -> values.add(replaceKeys((Map<String, Object>) item)));
      return values;
    } else if (value instanceof Map) {
      return replaceKeys((Map<String, Object>) value);
    } else {
      return value;
    }
  }

  private Boolean isInteger(String value) {
    try {
      Integer.parseInt(value);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private Boolean isFloat(String value) {
    try {
      Float.parseFloat(value);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private Boolean isLong(String value) {
    try {
      Long.parseLong(value);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private Boolean isBoolean(String value) {
    return value.toLowerCase().equals("true") || value.toLowerCase().equals("false");
  }
}
