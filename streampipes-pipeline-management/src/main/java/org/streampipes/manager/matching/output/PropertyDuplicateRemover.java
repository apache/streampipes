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

package org.streampipes.manager.matching.output;

import org.streampipes.empire.core.empire.SupportsRdfId;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyNested;
import org.streampipes.model.schema.EventPropertyPrimitive;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class PropertyDuplicateRemover {

  private List<EventProperty> existingProperties;
  private List<EventProperty> newProperties;

  public PropertyDuplicateRemover(List<EventProperty> existingProperties, List<EventProperty> newProperties) {
    this.existingProperties = existingProperties;
    this.newProperties = newProperties;
  }

  public List<EventProperty> rename() {

    List<EventProperty> newEventProperties = new ArrayList<>();
    for (EventProperty p : newProperties) {
      int i = 1;
      EventProperty newProperty = p;
      while (isAlreadyDefined(existingProperties, newProperty)) {
        if (newProperty instanceof EventPropertyPrimitive) {
          EventPropertyPrimitive primitive = (EventPropertyPrimitive) newProperty;
          newProperty = new EventPropertyPrimitive(primitive.getRuntimeType(), primitive.getRuntimeName() + i, "", primitive.getDomainProperties());
          newProperty.setRdfId(new SupportsRdfId.URIKey(URI.create(primitive.getElementId() + i)));
        }
        if (newProperty instanceof EventPropertyNested) {
          EventPropertyNested nested = (EventPropertyNested) newProperty;

          //TODO: hack
          List<EventProperty> nestedProperties = new ArrayList<>();

          for (EventProperty np : nested.getEventProperties()) {
            if (np instanceof EventPropertyPrimitive) {
              EventPropertyPrimitive thisPrimitive = (EventPropertyPrimitive) np;
              EventProperty newNested = new EventPropertyPrimitive(thisPrimitive.getRuntimeType(), thisPrimitive.getRuntimeName(), "", thisPrimitive.getDomainProperties());
              //newNested.setRdfId(new URIKey(URI.create("urn:fzi.de:sepa:" +UUID.randomUUID().toString())));
              newNested.setRdfId(new SupportsRdfId.URIKey(URI.create(thisPrimitive.getElementId())));
              nestedProperties.add(newNested);
            }

          }
          newProperty = new EventPropertyNested(nested.getRuntimeName() + i, nestedProperties);
          //newProperty = new EventPropertyNested(nested.getPropertyName() +i, nested.getEventProperties());
          //newProperty.setRdfId(new URIKey(URI.create("urn:fzi.de:sepa:" +UUID.randomUUID().toString())));
          newProperty.setRdfId(new SupportsRdfId.URIKey(URI.create(nested.getElementId() + i)));
        }
        i++;
      }
      newEventProperties.add(newProperty);
    }
    return newEventProperties;
  }

  private boolean isAlreadyDefined(List<EventProperty> existingProperties, EventProperty appendProperty) {
    for (EventProperty existingAppendProperty : existingProperties) {
      if (appendProperty.getRuntimeName().equals(existingAppendProperty.getRuntimeName())) {
        return true;
      }
    }
    return false;
  }
}
