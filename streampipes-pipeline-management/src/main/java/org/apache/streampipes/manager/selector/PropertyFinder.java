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
package org.apache.streampipes.manager.selector;

import org.apache.streampipes.model.constants.PropertySelectorConstants;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventSchema;

import java.util.Collections;
import java.util.List;

public class PropertyFinder {

  private EventSchema schema;
  private String[] propertySelectors;

  public PropertyFinder(EventSchema schema, String propertySelector) {
    this.schema = schema;
    this.propertySelectors = propertySelector.split(PropertySelectorConstants.PROPERTY_DELIMITER);
  }

  public List<EventProperty> findProperty() {
    return findProperty(schema.getEventProperties(), 1);
  }

  public List<EventProperty> findProperty(List<EventProperty> properties, Integer currentPointer) {
    for (EventProperty property : properties) {
      if (property.getRuntimeName().equals(propertySelectors[currentPointer])) {
        if (currentPointer == (propertySelectors.length - 1)) {
          return Collections.singletonList(property);
        } else {
          return findProperty(((EventPropertyNested) property).getEventProperties(),
              currentPointer + 1);
        }
      }
    }
    return Collections.emptyList();
  }
}
