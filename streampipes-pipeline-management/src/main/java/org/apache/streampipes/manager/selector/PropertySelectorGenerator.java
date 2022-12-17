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

import java.util.ArrayList;
import java.util.List;

public class PropertySelectorGenerator {

  private EventSchema firstSchema;
  private EventSchema secondSchema;
  private Boolean omitNestedProperties;

  public PropertySelectorGenerator(List<EventProperty> eventProperties, Boolean omitNestedProperties) {
    this.firstSchema = new EventSchema(eventProperties);
    this.omitNestedProperties = omitNestedProperties;
  }

  public PropertySelectorGenerator(EventSchema firstSchema, Boolean omitNestedProperties) {
    this.firstSchema = firstSchema;
    this.omitNestedProperties = omitNestedProperties;
  }

  public PropertySelectorGenerator(EventSchema firstSchema, EventSchema secondSchema, Boolean
      omitNestedProperties) {
    this.firstSchema = firstSchema;
    this.secondSchema = secondSchema;
    this.omitNestedProperties = omitNestedProperties;
  }

  public List<String> generateSelectors() {
    List<String> propertySelectors = new ArrayList<>();

    propertySelectors.addAll(generateSelectors(PropertySelectorUtils.getProperties(firstSchema),
        PropertySelectorConstants.FIRST_STREAM_ID_PREFIX));
    propertySelectors.addAll(generateSelectors(PropertySelectorUtils.getProperties(secondSchema),
        PropertySelectorConstants.SECOND_STREAM_ID_PREFIX));

    return propertySelectors;
  }

  public List<String> generateSelectors(String prefix) {
    return generateSelectors(this.firstSchema.getEventProperties(), prefix);
  }

  private List<String> generateSelectors(List<EventProperty> eventProperties, String prefix) {
    List<String> propertySelectors = new ArrayList<>();
    for (EventProperty ep : eventProperties) {
      if (ep instanceof EventPropertyNested) {
        propertySelectors.addAll(generateSelectors(((EventPropertyNested) ep).getEventProperties(),
            makeSelector(prefix, ep.getRuntimeName())));
      }
      if (!(ep instanceof EventPropertyNested) || !omitNestedProperties) {
        propertySelectors.add(makeSelector(prefix, ep.getRuntimeName()));
      }
    }
    return propertySelectors;
  }

  private String makeSelector(String prefix, String runtimeName) {
    return prefix
        + PropertySelectorConstants.PROPERTY_DELIMITER
        + runtimeName;
  }
}
