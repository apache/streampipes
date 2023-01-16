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

package org.apache.streampipes.test.generator;

import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyNested;

import java.util.ArrayList;
import java.util.List;

public class EventPropertyNestedTestBuilder
    extends EventPropertyTestBuilder<EventPropertyNested, EventPropertyNestedTestBuilder> {

  private List<EventProperty> eventProperties;

  protected EventPropertyNestedTestBuilder() {
    super(new EventPropertyNested());
    eventProperties = new ArrayList<>();
  }

  public static EventPropertyNestedTestBuilder create() {
    return new EventPropertyNestedTestBuilder();
  }

  public EventPropertyNestedTestBuilder withEventProperties(List<EventProperty> eventProperties) {
    this.eventProperties.addAll(eventProperties);
    return this;
  }

  public EventPropertyNestedTestBuilder withEventProperty(EventProperty eventProperty) {
    this.eventProperties.add(eventProperty);
    return this;
  }

  @Override
  protected EventPropertyNestedTestBuilder me() {
    return this;
  }

  @Override
  public EventPropertyNested build() {
    eventProperty.setEventProperties(eventProperties);
    return eventProperty;
  }

}
