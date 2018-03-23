/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.model;

import org.apache.commons.lang.RandomStringUtils;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.quality.EventStreamQualityDefinition;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.vocabulary.StreamPipes;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@RdfsClass(StreamPipes.DATA_SET)
@Entity
public class SpDataSet extends SpDataSequence {

  private static final String prefix = "urn:fzi.de:dataset:";

  @OneToOne(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.SUPPORTED_GROUNDING)
  private EventGrounding supportedGrounding;

  public SpDataSet(String uri, String name, String description, String iconUrl, List<EventStreamQualityDefinition>
          hasEventStreamQualities,
                      EventGrounding eventGrounding,
                      EventSchema eventSchema) {
    super(uri, name, description, iconUrl, hasEventStreamQualities, eventGrounding, eventSchema);
  }

  public SpDataSet(String uri, String name, String description, EventSchema eventSchema)
  {
    super(uri, name, description, eventSchema);
  }

  public SpDataSet() {
    super(prefix + RandomStringUtils.randomAlphabetic(6));
  }

  public SpDataSet(SpDataSet other) {
    super(other);
    if (other.getSupportedGrounding() != null) this.supportedGrounding = new EventGrounding(other.getSupportedGrounding());
  }

  public EventGrounding getSupportedGrounding() {
    return supportedGrounding;
  }

  public void setSupportedGrounding(EventGrounding supportedGrounding) {
    this.supportedGrounding = supportedGrounding;
  }
}
