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
import org.streampipes.model.runtime.field.PrimitiveField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventConverter {

  private Event event;

  public EventConverter(Event event) {
    this.event = event;
  }

  public Map<String, Object> toMap() {
    Map<String, Object> outMap = new HashMap<>();

    event.getFields().forEach((key, value) -> outMap.put(value.getFieldNameOut(), makeEntry(value)));

    return outMap;
  }

  private Object makeEntry(AbstractField value) {
    if (PrimitiveField.class.isInstance(value) || List.class.isInstance(value)) {
      return value.getRawValue();
    } else {
      Map<String, Object> outMap = new HashMap<>();
      value.getAsComposite().getRawValue().entrySet().forEach(entry -> outMap.put(entry.getValue
              ().getFieldNameOut(), makeEntry(entry.getValue())));
      return outMap;
    }
  }
}
