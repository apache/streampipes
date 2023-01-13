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

package org.apache.streampipes.extensions.management.util;

import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.vocabulary.SO;

import java.net.URI;
import java.util.List;
import java.util.Optional;

public class EventSchemaUtils {

  /**
   * Returns the timestamp property of an event schema as an {@code Optional}.
   *
   * <p> The method checks all properties if they are of type {@code EventPropertyPrimitive} and if their domain
   * properties contains the uri http://schema.org/DateTime </p>
   *
   * @param eventSchema the event schema for which the timestamp property is to be returned
   * @return an {@code Optional} containing the timestamp property, or an empty {@code Optional} if
   * no such property was found
   */
  public static Optional<EventPropertyPrimitive> getTimestampProperty(EventSchema eventSchema) {
    return getTimstampProperty(eventSchema.getEventProperties());
  }


  private static Optional<EventPropertyPrimitive> getTimstampProperty(List<EventProperty> eventProperties) {
    for (EventProperty ep : eventProperties) {
      if (ep instanceof EventPropertyPrimitive && ep.getDomainProperties().contains(URI.create(SO.DATE_TIME))) {
        return Optional.of((EventPropertyPrimitive) ep);
      }

      if (ep instanceof EventPropertyNested) {
        return getTimstampProperty(((EventPropertyNested) ep).getEventProperties());
      }
    }

    return Optional.empty();
  }
}
