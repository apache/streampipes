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

package org.streampipes.manager.matching.v2;

import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.client.matching.MatchingResultType;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyList;
import org.streampipes.model.schema.EventPropertyNested;
import org.streampipes.model.schema.EventPropertyPrimitive;

import java.util.List;

public class PropertyMatch extends AbstractMatcher<EventProperty, EventProperty> {

  public PropertyMatch() {
    super(MatchingResultType.PROPERTY_MATCH);
    // TODO Auto-generated constructor stub
  }

  @Override
  public boolean match(EventProperty offer, EventProperty requirement, List<MatchingResultMessage> errorLog) {
    // return true if the requirement is an any property
    if (isAnyProperty(requirement)) {
      return true;
    }

    if (!matchesType(offer, requirement)) {
      buildErrorMessage(errorLog, "The required type " + requirement.getClass().getSimpleName() +
              " is not present in the connected stream.");
      return false;
    } else {
      if (isPrimitive(requirement)) {
        return new PrimitivePropertyMatch().match(toPrimitive(offer), toPrimitive(requirement), errorLog);
      } else if (isList(requirement)) {
        return new ListPropertyMatch().match(toList(offer), toList(requirement), errorLog);
      } else if (isNested(requirement)) {
        return new NestedPropertyMatch().match(toNested(offer), toNested(requirement), errorLog);
      } else {
        return false;
      }
    }
  }

  private boolean isAnyProperty(EventProperty eventProperty) {
    return eventProperty instanceof EventPropertyPrimitive &&
            eventProperty.getDomainProperties() == null &&
            ((EventPropertyPrimitive) eventProperty).getMeasurementUnit() == null &&
            ((EventPropertyPrimitive) eventProperty).getRuntimeType() == null;
  }

  private EventPropertyNested toNested(EventProperty property) {
    return (EventPropertyNested) property;
  }

  private EventPropertyList toList(EventProperty property) {
    return (EventPropertyList) property;
  }

  private EventPropertyPrimitive toPrimitive(EventProperty property) {
    return (EventPropertyPrimitive) property;
  }

  private boolean isList(EventProperty requirement) {
    return requirement instanceof EventPropertyList;
  }

  private boolean isNested(EventProperty requirement) {
    return requirement instanceof EventPropertyNested;
  }

  private boolean isPrimitive(EventProperty requirement) {
    return requirement instanceof EventPropertyPrimitive;
  }

  public boolean matchesType(EventProperty offer, EventProperty requirement) {
    return offer.getClass().getCanonicalName().equals(requirement.getClass().getCanonicalName());
  }


}
