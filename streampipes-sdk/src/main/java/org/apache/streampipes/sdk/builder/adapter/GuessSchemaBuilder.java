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
package org.apache.streampipes.sdk.builder.adapter;

import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.connect.guess.GuessTypeInfo;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuessSchemaBuilder {

  private List<EventProperty> eventProperties;
  private Map<String, GuessTypeInfo> samples;

  private GuessSchemaBuilder() {
    this.eventProperties = new ArrayList<>();
    this.samples = new HashMap<>();
  }

  /**
   * Creates a new guess schema object using the builder pattern.
   */
  public static GuessSchemaBuilder create() {
    return new GuessSchemaBuilder();
  }

  public GuessSchemaBuilder sample(String runtimeName,
                                   Object sampleValue) {
    this.samples.put(runtimeName, new GuessTypeInfo(sampleValue.getClass().getCanonicalName(), sampleValue));

    return this;
  }

  public GuessSchemaBuilder property(EventProperty property) {
    this.eventProperties.add(property);

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
    guessSchema.setEventPreview(List.of(this.samples));

    return guessSchema;
  }
}
