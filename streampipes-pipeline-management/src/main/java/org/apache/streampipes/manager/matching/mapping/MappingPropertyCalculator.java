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

package org.apache.streampipes.manager.matching.mapping;

import org.apache.streampipes.manager.matching.v2.PropertyMatch;
import org.apache.streampipes.manager.selector.PropertyFinder;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;

import java.util.ArrayList;
import java.util.List;

public class MappingPropertyCalculator {

  private EventSchema schema;
  private List<String> availablePropertySelectors;
  private EventProperty requirement;

  public MappingPropertyCalculator() {
  }

  public MappingPropertyCalculator(EventSchema schema, List<String> availablePropertySelectors,
                                   EventProperty requirement) {
    this.schema = schema;
    this.availablePropertySelectors = availablePropertySelectors;
    this.requirement = requirement;
  }

  public List<String> matchedPropertySelectors() {
    List<String> matchedSelectors = new ArrayList<>();
    for (String propertySelector : availablePropertySelectors) {
      List<EventProperty> offeredProperties = getEventProperty(propertySelector);
      if (offeredProperties.size() == 1) {
        if (new PropertyMatch().match(offeredProperties.get(0), requirement, new ArrayList<>())) {
          matchedSelectors.add(propertySelector);
        }
      }
    }

    return matchedSelectors;
  }

  private List<EventProperty> getEventProperty(String propertySelector) {
    return new PropertyFinder(schema, propertySelector).findProperty();
  }
}
