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

package org.streampipes.manager.matching.v2.mapping;

import org.streampipes.manager.matching.v2.PropertyMatch;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyList;
import org.streampipes.model.schema.EventPropertyNested;
import org.streampipes.model.schema.EventPropertyPrimitive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MappingPropertyCalculator {


  public MappingPropertyCalculator() {
  }

  public List<EventProperty> matchesProperties(List<EventProperty> offeredProperties,
                                               EventProperty requirement) {
    List<EventProperty> allMatchingProperties = new ArrayList<>();
    for (EventProperty offer : offeredProperties) {
      allMatchingProperties.addAll(matches(offer, requirement));
    }

    return allMatchingProperties;
  }

  private List<EventProperty> matches(EventProperty offer, EventProperty requirement) {
    if (type(requirement, EventPropertyPrimitive.class)) {
      if (offer instanceof EventPropertyPrimitive) {
        if (new PropertyMatch().match(offer, requirement, new ArrayList<>())) {
            return Collections.singletonList(offer);
        }
      } else if (offer instanceof EventPropertyNested) {
        EventPropertyNested clonedOffer = new EventPropertyNested((EventPropertyNested) offer);
        clonedOffer.setEventProperties(matchesProperties(clonedOffer.getEventProperties(), requirement));
        return Collections.singletonList(clonedOffer);
      }
    } else if (requirement instanceof EventPropertyList) {
      if (offer instanceof EventPropertyList) {
        if (matchesList((EventPropertyList) offer, (EventPropertyList) requirement)) {
          return Collections.singletonList(offer);
        }
      }

    } else if (requirement instanceof EventPropertyNested) {
      EventPropertyNested rightNested = (EventPropertyNested) requirement;
      for (EventProperty nestedProperty : rightNested.getEventProperties()) {
        // TODO
      }
    }
    return Collections.emptyList();
  }

  private Boolean type(EventProperty eventProperty, Class<? extends EventProperty> clazz) {
    return clazz.isInstance(eventProperty);
  }

  public boolean matchesList(EventPropertyList offer, EventPropertyList requirement) {
    boolean match = true;
    for (EventProperty p : requirement.getEventProperties()) {
      if (!matches(offer.getEventProperties(), p)) {
        match = false;
      }
    }
    return match;
  }

  public boolean matches(List<EventProperty> offer, EventProperty requirement) {
    boolean match = false;
    for (EventProperty of : offer) {
      // TODO
    }
    return match;
  }

}
