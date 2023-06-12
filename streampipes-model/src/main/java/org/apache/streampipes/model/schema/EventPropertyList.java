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

import java.net.URI;
import java.util.List;
import java.util.Objects;

public class EventPropertyList extends EventProperty {

  private static final long serialVersionUID = -2636018143426727534L;

  private EventProperty eventProperty;

  public EventPropertyList() {
    super();
  }

  public EventPropertyList(EventPropertyList other) {
    super(other);
    if (other.getEventProperty() != null) {
      this.eventProperty = new Cloner().property(other.getEventProperty());
    }
  }

  public EventPropertyList(String propertyName, EventProperty listProperty) {
    super(propertyName);
    this.eventProperty = listProperty;
  }

  public EventPropertyList(EventProperty listProperty) {
    super();
    this.eventProperty = listProperty;
  }

  public EventPropertyList(String propertyName, EventProperty eventProperty, List<URI> domainProperties) {
    super(propertyName);
    this.eventProperty = eventProperty;
    this.setDomainProperties(domainProperties);
  }

  public EventProperty getEventProperty() {
    return eventProperty;
  }

  public void setEventProperty(EventProperty eventProperty) {
    this.eventProperty = eventProperty;
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
    EventPropertyList that = (EventPropertyList) o;
    return Objects.equals(eventProperty, that.eventProperty);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventProperty);
  }
}
