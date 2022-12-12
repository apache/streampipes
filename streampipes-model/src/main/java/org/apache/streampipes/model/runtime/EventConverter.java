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

import org.apache.streampipes.model.runtime.field.AbstractField;
import org.apache.streampipes.model.runtime.field.ListField;
import org.apache.streampipes.model.runtime.field.PrimitiveField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventConverter {

  private Event event;

  public EventConverter(Event event) {
    this.event = event;
  }

  public Map<String, Object> toMap(Boolean renameProperties) {
    Map<String, Object> outMap = new HashMap<>();

    event.getFields()
        .forEach((key, value) -> outMap.put(getValue(value, renameProperties), makeEntry(value, renameProperties)));

    return outMap;
  }

  public Map<String, Object> toMap() {
    return toMap(true);
  }

  public Map<String, Object> toInputEventMap() {
    return toMap(false);
  }

  private Object makeEntry(AbstractField value, Boolean renameProperties) {
    if (value instanceof PrimitiveField) {
      return value.getRawValue();
    } else if (value instanceof ListField) {
      List<Object> objects = new ArrayList<>();
      for (AbstractField field : value.getAsList().getRawValue()) {
        objects.add(makeEntry(field, renameProperties));
      }
      return objects;
    } else {
      Map<String, Object> outMap = new HashMap<>();
      value.getAsComposite()
          .getRawValue()
          .forEach(
              (key, value1) -> outMap.put(getValue(value1, renameProperties), makeEntry(value1, renameProperties)));
      return outMap;
    }
  }

  private String getValue(AbstractField field, Boolean renameProperties) {
    return renameProperties ? field.getFieldNameOut() : field.getFieldNameIn();
  }
}
