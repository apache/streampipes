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

package org.apache.streampipes.model.schema;

import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.model.util.ListUtils;

import java.util.ArrayList;
import java.util.List;

public class EventSchema {

  private static final long serialVersionUID = -3994041794693686406L;

  private List<EventProperty> eventProperties;

  public EventSchema(List<EventProperty> eventProperties) {
    super();
    this.eventProperties = eventProperties;
  }

  public EventSchema() {
    super();
    this.eventProperties = new ArrayList<>();
  }

  public EventSchema(EventSchema other) {
    this.eventProperties = new Cloner().properties(other.getEventProperties());
  }

  public List<EventProperty> getEventProperties() {
    return eventProperties;
  }

  public void setEventProperties(List<EventProperty> eventProperties) {
    this.eventProperties = eventProperties;
  }


  public boolean addEventProperty(EventProperty p) {
    return eventProperties.add(p);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EventSchema that = (EventSchema) o;
    return ListUtils.isEqualList(this.eventProperties, that.eventProperties);
  }

  @Override
  public String toString() {
    return "EventSchema{"
           + "eventProperties=" + eventProperties
           + '}';
  }
}
