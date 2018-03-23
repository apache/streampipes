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
package org.streampipes.model.graph;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

public class DataSetInvocation extends NamedStreamPipesEntity {

  @OneToOne(fetch = FetchType.EAGER,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @RdfProperty(StreamPipes.HAS_GROUNDING)
  private EventGrounding eventGrounding;

  public DataSetInvocation(DataSetInvocation other) {
    if (other.getEventGrounding() != null) this.eventGrounding = new EventGrounding(other.getEventGrounding());
  }

  public EventGrounding getEventGrounding() {
    return eventGrounding;
  }

  public void setEventGrounding(EventGrounding eventGrounding) {
    this.eventGrounding = eventGrounding;
  }
}
