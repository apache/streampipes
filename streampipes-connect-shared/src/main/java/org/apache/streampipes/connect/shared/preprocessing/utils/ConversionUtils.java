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

package org.apache.streampipes.connect.shared.preprocessing.utils;

import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyList;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;

import java.util.Collections;
import java.util.List;

public class ConversionUtils {

  public static EventProperty findProperty(List<EventProperty> properties,
                                           String runtimeKey) {
    return findProperty(properties, Utils.toKeyArray(runtimeKey));
  }

  public static EventPropertyPrimitive findPrimitiveProperty(List<EventProperty> properties,
                                                       String runtimeKey) {
    return (EventPropertyPrimitive) findProperty(properties, runtimeKey);
  }

  public static List<EventProperty> findPropertyHierarchy(List<EventProperty> originalProperties,
                                                          String runtimeKeys) {
    return findPropertyHierarchy(originalProperties, Utils.toKeyArray(runtimeKeys));
  }

  public static List<EventProperty> findPropertyHierarchy(List<EventProperty> originalProperties,
                                                          List<String> runtimeKeys) {
    if (runtimeKeys.isEmpty()) {
      return Collections.emptyList();
    } else if (runtimeKeys.size() == 1) {
      return originalProperties;
    } else {
      List<String> finalRuntimeKeys = runtimeKeys;
      var properties = originalProperties
          .stream()
          .filter(ep -> ep.getRuntimeName().equals(finalRuntimeKeys.get(0)))
          .toList();
      if (!properties.isEmpty()) {
        runtimeKeys = runtimeKeys.subList(1, runtimeKeys.size());
        EventPropertyNested nestedProperty = (EventPropertyNested) properties.get(0);
        return findPropertyHierarchy(nestedProperty.getEventProperties(), runtimeKeys);
      } else {
        return Collections.emptyList();
      }
    }
  }

  public static EventProperty findProperty(List<EventProperty> originalProperties,
                                           List<String> runtimeKeys) {
    if (runtimeKeys.isEmpty()) {
      throw new IllegalArgumentException("Could not find property");
    }

    String firstRuntimeKey = runtimeKeys.get(0);

    for (EventProperty property : originalProperties) {
      if (property.getRuntimeName().equals(firstRuntimeKey)) {
        if (property instanceof EventPropertyPrimitive || property instanceof EventPropertyList) {
          return property;
        } else if (runtimeKeys.size() > 1) {
          return findProperty(
              ((EventPropertyNested) property).getEventProperties(),
              runtimeKeys.subList(1, runtimeKeys.size())
          );
        } else {
          return property;
        }
      }
    }

    throw new IllegalArgumentException("Could not find property");
  }
}
