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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventPropertyNested extends EventProperty {

  private static final long serialVersionUID = 6565569954878135195L;

  private List<EventProperty> eventProperties;

  public EventPropertyNested() {
    super();
    this.eventProperties = new ArrayList<>();
  }

  public EventPropertyNested(EventPropertyNested other) {
    super(other);
    if (other.eventProperties != null) {
      this.eventProperties = new Cloner().properties(other.getEventProperties());
    } else {
      this.eventProperties = new ArrayList<>();
    }
  }

  public EventPropertyNested(String propertyName, List<EventProperty> eventProperties) {
    super(propertyName);
    this.eventProperties = eventProperties;
  }

  public EventPropertyNested(String propertyName) {
    super(propertyName);
    this.eventProperties = new ArrayList<>();

  }

  public List<EventProperty> getEventProperties() {
    return eventProperties;
  }

  public void setEventProperties(List<EventProperty> eventProperties) {
    this.eventProperties = eventProperties;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    EventPropertyNested that = (EventPropertyNested) o;
    return Objects.equals(eventProperties, that.eventProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventProperties);
  }
}
