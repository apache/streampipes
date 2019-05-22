/*
Copyright 2019 FZI Forschungszentrum Informatik

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
package org.streampipes.sdk.builder.adapter;

import org.streampipes.model.connect.guess.DomainPropertyProbabilityList;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventSchema;

import java.util.ArrayList;
import java.util.List;

public class GuessSchemaBuilder {

  private List<EventProperty> eventProperties;
  private List<DomainPropertyProbabilityList> domainPropertyProbabilitiesList;

  private GuessSchemaBuilder() {
    this.eventProperties = new ArrayList<>();
    this.domainPropertyProbabilitiesList = new ArrayList<>();
  }

  /**
   * Creates a new guess schema object using the builder pattern.
   */
  public static GuessSchemaBuilder create() {
    return new GuessSchemaBuilder();
  }

  public GuessSchemaBuilder property(EventProperty property) {
    this.eventProperties.add(property);

    return this;
  }

  public GuessSchemaBuilder domainPropertyProbability(DomainPropertyProbabilityList domainPropertyProbabilityList) {
    this.domainPropertyProbabilitiesList.add(domainPropertyProbabilityList);

    return this;
  }

  public GuessSchema build() {
    GuessSchema guessSchema = new GuessSchema();
    EventSchema eventSchema = new EventSchema();

    for (int i = 0; i < eventProperties.size(); i++) {
      eventProperties.get(i).setIndex(i);
    }

    eventSchema.setEventProperties(eventProperties);

    guessSchema.setEventSchema(eventSchema);
    guessSchema.setPropertyProbabilityList(domainPropertyProbabilitiesList);

    return guessSchema;
  }
}
