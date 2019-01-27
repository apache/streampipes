/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.model.runtime;

import com.google.gson.internal.LinkedTreeMap;
import org.streampipes.model.constants.PropertySelectorConstants;
import org.streampipes.model.runtime.field.AbstractField;
import org.streampipes.model.runtime.field.CompositeField;
import org.streampipes.model.runtime.field.ListField;
import org.streampipes.model.runtime.field.PrimitiveField;

import java.util.List;
import java.util.Map;

public class EventFactory {

  public static Event fromMap(Map<String, Object> event, SourceInfo sourceInfo, SchemaInfo
          schemaInfo) {

    Map<String, AbstractField> fields = new LinkedTreeMap<>();

    for (String key : event.keySet()) {
      String currentSelector = makeSelector(key, sourceInfo.getSelectorPrefix());
      fields.put(currentSelector, makeField(key, event.get(key), currentSelector, schemaInfo));
    }

    return new Event(event, fields, sourceInfo, schemaInfo);
  }

  private static AbstractField makeField(String runtimeName, Object o, String currentSelector,
                                         SchemaInfo schemaInfo) {
    if (Map.class.isInstance(o)) {
      Map<String, Object> items = (Map<String, Object>) o;
      Map<String, AbstractField> fieldMap = new LinkedTreeMap<>();
      for (String key : items.keySet()) {
        String selector = makeSelector(key, currentSelector);
        fieldMap.put(selector, makeField(key, items.get(key), selector, schemaInfo));
      }
      // TODO get new runtimeName
      return new CompositeField(runtimeName, runtimeName, fieldMap);
    } else if (List.class.isInstance(o)) {
      // TODO get new runtimeName
      return new ListField(runtimeName, runtimeName, (List<Object>) o);
    } else {
      // TODO get new runtimeName
      return new PrimitiveField(runtimeName, runtimeName, o);
    }
  }

  private static String makeSelector(String key, String selectorPrefix) {
    return selectorPrefix + PropertySelectorConstants.PROPERTY_DELIMITER + key;
  }
}
