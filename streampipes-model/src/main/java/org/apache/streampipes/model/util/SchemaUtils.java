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

package org.apache.streampipes.model.util;

import org.apache.streampipes.model.schema.EventProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchemaUtils {

  public static Map<String, Object> toRuntimeMap(List<EventProperty> eps) {
    return toUntypedRuntimeMap(eps);
  }

  public static Map<String, Object> toUntypedRuntimeMap(List<EventProperty> eps) {
    Map<String, Object> propertyMap = new HashMap<>();

    for (EventProperty p : eps) {
      propertyMap.putAll(PropertyUtils.getUntypedRuntimeFormat(p));
    }
    return propertyMap;
  }

  public static List<String> toPropertyList(List<EventProperty> eps) {
    List<String> properties = new ArrayList<>();

    for (EventProperty p : eps) {
      properties.addAll(PropertyUtils.getFullPropertyName(p, ""));
    }
    return properties;
  }

}
