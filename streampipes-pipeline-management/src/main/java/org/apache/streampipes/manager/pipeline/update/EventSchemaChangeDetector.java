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

package org.apache.streampipes.manager.pipeline.update;

import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyList;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class EventSchemaChangeDetector {

  private EventSchemaChangeDetector() {
  }

  static boolean hasChanged(EventSchema previous, EventSchema updated) {
    return !sameProperties(previous == null ? null : previous.getEventProperties(),
        updated == null ? null : updated.getEventProperties());
  }

  private static boolean sameProperties(List<EventProperty> previous, List<EventProperty> updated) {
    var previousProperties = previous == null ? List.<EventProperty>of() : previous;
    var unmatched = new ArrayList<>(updated == null ? List.<EventProperty>of() : updated);
    if (previousProperties.size() != unmatched.size()) {
      return false;
    }
    // Match fields independently of order, including fields within nested objects.
    for (var property : previousProperties) {
      boolean matched = false;
      for (int i = 0; i < unmatched.size(); i++) {
        if (sameProperty(property, unmatched.get(i))) {
          unmatched.remove(i);
          matched = true;
          break;
        }
      }
      if (!matched) {
        return false;
      }
    }
    return true;
  }

  private static boolean sameProperty(EventProperty previous, EventProperty updated) {
    if (previous == null || updated == null) {
      return previous == updated;
    }
    if (previous.getClass() != updated.getClass()
        || !Objects.equals(previous.getRuntimeName(), updated.getRuntimeName())
        || !Objects.equals(previous.getPropertyScope(), updated.getPropertyScope())) {
      return false;
    }
    if (previous instanceof EventPropertyPrimitive primitive) {
      return Objects.equals(primitive.getRuntimeType(), ((EventPropertyPrimitive) updated).getRuntimeType());
    }
    if (previous instanceof EventPropertyNested nested) {
      return sameProperties(nested.getEventProperties(), ((EventPropertyNested) updated).getEventProperties());
    }
    if (previous instanceof EventPropertyList list) {
      return sameProperty(list.getEventProperty(), ((EventPropertyList) updated).getEventProperty());
    }
    return true;
  }
}
