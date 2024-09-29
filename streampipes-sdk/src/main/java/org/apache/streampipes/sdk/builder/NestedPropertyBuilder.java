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
package org.apache.streampipes.sdk.builder;

import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyNested;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder class for creating instances of {@link EventPropertyNested}.
 */
public class NestedPropertyBuilder {

  /**
   * The {@link EventPropertyNested} being constructed by this builder.
   */
  private final EventPropertyNested eventPropertyNested;

  /**
   * List of {@link EventProperty} instances contained within the {@link EventPropertyNested}.
   */
  private final List<EventProperty> containedEventProperties;

  private NestedPropertyBuilder(String propertyName) {
    if (propertyName == null || propertyName.isEmpty()) {
      throw new IllegalArgumentException("Property name cannot be null or empty");
    }
    this.eventPropertyNested = new EventPropertyNested(propertyName);
    this.containedEventProperties = new ArrayList<>();
  }

  /**
   * Static factory method to create a new instance of the builder.
   *
   * @param propertyName
   *          The name of the property.
   * @return A new instance of {@code NestedPropertyBuilder}.
   */
  public static NestedPropertyBuilder create(String propertyName) {
    return new NestedPropertyBuilder(propertyName);
  }

  /**
   * Adds a single {@link EventProperty} to the list of nested properties.
   *
   * @param eventProperty
   *          The {@link EventProperty} to add.
   * @return This builder instance for method chaining.
   */
  public NestedPropertyBuilder withEventProperty(EventProperty eventProperty) {
    this.containedEventProperties.add(eventProperty);
    return this;
  }

  /**
   * Adds multiple {@link EventProperty} instances to the list of nested properties.
   *
   * @param eventProperties
   *          The array of {@link EventProperty} instances to add.
   * @return This builder instance for method chaining.
   */
  public NestedPropertyBuilder withEventProperties(EventProperty... eventProperties) {
    this.containedEventProperties.addAll(List.of(eventProperties));
    return this;
  }

  /**
   * Builds and returns the final {@link EventPropertyNested} instance. Note: This method creates a new instance to
   * ensure immutability.
   *
   * @return The constructed {@link EventPropertyNested}.
   */
  public EventPropertyNested build() {
    // Create a new instance to make the class immutable
    EventPropertyNested built = new EventPropertyNested(this.eventPropertyNested.getRuntimeName());
    built.setEventProperties(containedEventProperties);
    return built;
  }
}
