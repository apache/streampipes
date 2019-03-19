/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.model.schema;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import java.net.URI;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@RdfsClass(StreamPipes.EVENT_PROPERTY_LIST)
@Entity
public class EventPropertyList extends EventProperty {

  private static final long serialVersionUID = -2636018143426727534L;

  @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_EVENT_PROPERTY)
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
}
