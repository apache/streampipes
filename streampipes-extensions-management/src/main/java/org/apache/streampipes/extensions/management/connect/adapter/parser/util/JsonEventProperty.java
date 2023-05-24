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

package org.apache.streampipes.extensions.management.connect.adapter.parser.util;

import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyList;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.vocabulary.XSD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonEventProperty {

  private static final Logger LOG = LoggerFactory.getLogger(JsonEventProperty.class);

  public static EventProperty getEventProperty(String key, Object o) {
    EventProperty resultProperty = null;

    if (o == null) {
      resultProperty = makePrimitiveProperty(key, XSD.STRING.toString());
    } else if (o.getClass().equals(Boolean.class)) {
      resultProperty = makePrimitiveProperty(key, XSD.BOOLEAN.toString());
    } else if (o.getClass().equals(String.class)) {
      resultProperty = makePrimitiveProperty(key, XSD.STRING.toString());
    } else if (o.getClass().equals(Integer.class) || o.getClass().equals(Double.class)
               || o.getClass().equals(Float.class) || o.getClass().equals(Long.class)
               || o.getClass().equals(BigDecimal.class)) {
      resultProperty = makePrimitiveProperty(key, XSD.FLOAT.toString());
    } else if (o.getClass().equals(LinkedHashMap.class)) {
      resultProperty = new EventPropertyNested();
      resultProperty.setRuntimeName(key);
      List<EventProperty> all = new ArrayList<>();
      for (Map.Entry<String, Object> entry : ((Map<String, Object>) o).entrySet()) {
        all.add(getEventProperty(entry.getKey(), entry.getValue()));
      }

      ((EventPropertyNested) resultProperty).setEventProperties(all);

    } else if (o.getClass().equals(ArrayList.class)) {
      resultProperty = new EventPropertyList();
      ArrayList content = (ArrayList) o;

      EventPropertyPrimitive arrayContent = new EventPropertyPrimitive();
      if (content.size() == 0) {
        arrayContent.setRuntimeType(XSD.STRING.toString());
      } else if (content.get(0) instanceof Integer || content.get(0) instanceof Double
                 || content.get(0) instanceof Long) {
        arrayContent.setRuntimeType(XSD.FLOAT.toString());
      } else if (content.get(0) instanceof Boolean) {
        arrayContent.setRuntimeType(XSD.BOOLEAN.toString());
      } else {
        arrayContent.setRuntimeType(XSD.STRING.toString());
      }

      ((EventPropertyList) resultProperty).setEventProperty(arrayContent);
      resultProperty.setRuntimeName(key);

    }

    if (resultProperty == null) {
      LOG.error("Property Type was not detected in JsonParser for the schema detection. "
                + "This should never happen!");
    }

    resultProperty.setDescription("");
    return resultProperty;
  }

  private static EventProperty makePrimitiveProperty(String key,
                                                     String runtimeType) {
    var property = new EventPropertyPrimitive();
    property.setRuntimeName(key);
    property.setRuntimeType(runtimeType);
    property.setPropertyScope(PropertyScope.MEASUREMENT_PROPERTY.name());

    return property;
  }
}
