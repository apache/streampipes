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

package org.apache.streampipes.manager.matching.output;

import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class PropertyDuplicateRemover {

  private List<EventProperty> existingProperties;
  private List<EventProperty> newProperties;

  public PropertyDuplicateRemover(List<EventProperty> existingProperties, List<EventProperty> newProperties) {
    this.existingProperties = existingProperties;
    this.newProperties = newProperties;
  }

  public List<EventProperty> rename() {

    List<EventProperty> newEventProperties = new ArrayList<>();
    for (EventProperty p : newProperties) {
      int i = 1;
      EventProperty newProperty = p;
      while (isAlreadyDefined(existingProperties, newProperty)) {
        if (newProperty instanceof EventPropertyPrimitive) {
          EventPropertyPrimitive primitive = (EventPropertyPrimitive) newProperty;
          newProperty = new EventPropertyPrimitive(primitive.getRuntimeType(), primitive.getRuntimeName() + i, "",
              primitive.getDomainProperties());
        }
        if (newProperty instanceof EventPropertyNested) {
          EventPropertyNested nested = (EventPropertyNested) newProperty;

          //TODO: hack
          List<EventProperty> nestedProperties = new ArrayList<>();

          for (EventProperty np : nested.getEventProperties()) {
            if (np instanceof EventPropertyPrimitive) {
              EventPropertyPrimitive thisPrimitive = (EventPropertyPrimitive) np;
              EventProperty newNested =
                  new EventPropertyPrimitive(thisPrimitive.getRuntimeType(), thisPrimitive.getRuntimeName(), "",
                      thisPrimitive.getDomainProperties());
              nestedProperties.add(newNested);
            }

          }
          newProperty = new EventPropertyNested(nested.getRuntimeName() + i, nestedProperties);
          //newProperty = new EventPropertyNested(nested.getPropertyName() +i, nested.getEventProperties());
        }
        i++;
      }
      newEventProperties.add(newProperty);
    }
    return newEventProperties;
  }

  private boolean isAlreadyDefined(List<EventProperty> existingProperties, EventProperty appendProperty) {
    for (EventProperty existingAppendProperty : existingProperties) {
      if (appendProperty.getRuntimeName().equals(existingAppendProperty.getRuntimeName())) {
        return true;
      }
    }
    return false;
  }
}
