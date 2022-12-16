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
package org.apache.streampipes.model.runtime;

import org.apache.streampipes.model.constants.PropertySelectorConstants;
import org.apache.streampipes.model.output.PropertyRenameRule;
import org.apache.streampipes.model.runtime.field.AbstractField;
import org.apache.streampipes.model.runtime.field.ListField;
import org.apache.streampipes.model.runtime.field.NestedField;
import org.apache.streampipes.model.runtime.field.PrimitiveField;
import org.apache.streampipes.model.schema.EventSchema;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventFactory {

  public static Event fromEvents(Event firstEvent, Event secondEvent, EventSchema outputSchema) {
    Map<String, AbstractField> fieldMap = new HashMap<>();
    fieldMap.putAll(firstEvent.getFields());
    fieldMap.putAll(secondEvent.getFields());

    return new Event(fieldMap, makeMergedSourceInfo(), makeMergedSchemaInfo(firstEvent, secondEvent,
        outputSchema));
  }

  private static SourceInfo makeMergedSourceInfo() {
    return new SourceInfo(null, null);
  }

  private static SchemaInfo makeMergedSchemaInfo(Event firstEvent, Event secondEvent, EventSchema
      outputSchema) {
    List<PropertyRenameRule> propertyRenameRules = new ArrayList<>();
    propertyRenameRules.addAll(firstEvent.getSchemaInfo().getRenameRules());
    propertyRenameRules.addAll(secondEvent.getSchemaInfo().getRenameRules());
    return new SchemaInfo(outputSchema, propertyRenameRules);
  }

  public static Event fromMap(Map<String, Object> event) {
    // TODO provide output event schema through RuntimeContext
    SourceInfo sourceInfo = new SourceInfo("o", "o");
    SchemaInfo schemaInfo = new SchemaInfo(null, new ArrayList<>());
    return fromMap(event, sourceInfo, schemaInfo);
  }

  public static Event fromMap(Map<String, Object> event,
                              SourceInfo sourceInfo,
                              SchemaInfo schemaInfo) {

    Map<String, AbstractField> fields = new LinkedTreeMap<>();
    String selectorPrefix = sourceInfo.getSelectorPrefix();

    event.keySet().forEach(key -> {
      String currentSelector = makeSelector(key, selectorPrefix);
      fields.put(currentSelector, makeField(key, event.get(key), currentSelector, schemaInfo));
    });

    return new Event(fields, sourceInfo, schemaInfo);
  }

  public static Event makeSubset(Event event, List<String> fieldSelectors) {
    Map<String, AbstractField> fieldMap = makeFieldMap(event.getFields(), fieldSelectors);
    return new Event(fieldMap, event.getSourceInfo(), event.getSchemaInfo());
  }

  private static Map<String, Object> makeRuntimeMapSubset(Map<String, Object> event, List<String>
      fieldSelectors, String currentPrefix) {
    Map<String, Object> outMap = new HashMap<>();
    for (String key : event.keySet()) {
      if (contains(makeSelector(key, currentPrefix), fieldSelectors)) {
        Object object = event.get(key);
        if (Map.class.isInstance(object)) {
          Map<String, Object> map = (Map<String, Object>) object;
          map.put(key, makeRuntimeMapSubset(map, fieldSelectors, makeSelector(key, currentPrefix)));
        } else {
          outMap.put(key, object);
        }
      }
    }
    return outMap;
  }

  private static Map<String, AbstractField> makeFieldMap(Map<String, AbstractField> fields,
                                                         List<String> fieldSelectors) {
    Map<String, AbstractField> outMap = new HashMap<>();
    for (String key : fields.keySet()) {
      if (contains(key, fieldSelectors)) {
        AbstractField field = fields.get(key);
        if (PrimitiveField.class.isInstance(field) || ListField.class.isInstance(field)) {
          outMap.put(key, field);
        } else {
          field.getAsComposite().setValue(makeFieldMap(field.getAsComposite().getRawValue(),
              fieldSelectors));
          outMap.put(key, field);
        }
      }
    }

    return outMap;
  }

  private static boolean contains(String key, List<String> fieldSelectors) {
    return fieldSelectors.stream().anyMatch(f -> f.equals(key));
  }

  private static AbstractField makeField(String runtimeName, Object o, String currentSelector,
                                         SchemaInfo schemaInfo) {
    if (o instanceof Map) {
      Map<String, Object> items = (Map<String, Object>) o;
      Map<String, AbstractField> fieldMap = new LinkedTreeMap<>();
      items.forEach((key, value) -> {
        String selector = makeSelector(key, currentSelector);
        fieldMap.put(selector, makeField(key, value, selector, schemaInfo));
      });
      return new NestedField(runtimeName, getNewRuntimeName(currentSelector, runtimeName,
          schemaInfo.getRenameRules()),
          fieldMap);
    } else if (o instanceof List) {
      List<AbstractField> items = new ArrayList<>();
      for (Integer i = 0; i < ((List) o).size(); i++) {
        items.add(makeField("", ((List) o).get(i), currentSelector + "::" + i, schemaInfo));
      }
      return new ListField(runtimeName, getNewRuntimeName(currentSelector, runtimeName, schemaInfo
          .getRenameRules()), items);
    } else {
      return new PrimitiveField(runtimeName, getNewRuntimeName(currentSelector, runtimeName,
          schemaInfo.getRenameRules()), o);
    }
  }

  private static String getNewRuntimeName(String currentSelector, String
      runtimeName, List<PropertyRenameRule>
                                              renameRules) {
    return renameRules
        .stream()
        .filter(r -> r.getRuntimeId().equals(currentSelector))
        .findFirst()
        .map(PropertyRenameRule::getNewRuntimeName).orElse(runtimeName);
  }

  private static String makeSelector(String key, String selectorPrefix) {
    return selectorPrefix + PropertySelectorConstants.PROPERTY_DELIMITER + key;
  }
}
