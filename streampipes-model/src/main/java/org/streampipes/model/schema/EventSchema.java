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

import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/"})
@RdfsClass(StreamPipes.EVENT_SCHEMA)
@Entity
public class EventSchema extends UnnamedStreamPipesEntity {

  private static final long serialVersionUID = -3994041794693686406L;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_EVENT_PROPERTY)
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
    super(other);
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

}
