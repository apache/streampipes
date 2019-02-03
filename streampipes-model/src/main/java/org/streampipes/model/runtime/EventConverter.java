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

import org.streampipes.model.runtime.field.AbstractField;
import org.streampipes.model.runtime.field.ListField;
import org.streampipes.model.runtime.field.PrimitiveField;

import java.util.HashMap;
import java.util.Map;

public class EventConverter {

  private Event event;

  public EventConverter(Event event) {
    this.event = event;
  }

  public Map<String, Object> toMap(Boolean renameProperties) {
    Map<String, Object> outMap = new HashMap<>();

    event.getFields().forEach((key, value) -> outMap.put(getValue(value, renameProperties), makeEntry
            (value, renameProperties)));

    return outMap;
  }

  public Map<String, Object> toMap() {
    return toMap(true);
  }

  public Map<String, Object> toInputEventMap() {
    return toMap(false);
  }

  private Object makeEntry(AbstractField value, Boolean renameProperties) {
    if (PrimitiveField.class.isInstance(value) || ListField.class.isInstance(value)) {
      return value.getRawValue();
    } else {
      Map<String, Object> outMap = new HashMap<>();
      value.getAsComposite().getRawValue().entrySet().forEach(entry -> outMap.put(getValue(entry
              .getValue
              (), renameProperties), makeEntry(entry.getValue(), renameProperties)));
      return outMap;
    }
  }

  private String getValue(AbstractField field, Boolean renameProperties) {
    return renameProperties ? field.getFieldNameOut() : field.getFieldNameIn();
  }
}
