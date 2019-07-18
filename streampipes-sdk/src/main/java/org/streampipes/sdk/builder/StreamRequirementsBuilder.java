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

package org.streampipes.sdk.builder;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.model.staticproperty.MappingProperty;
import org.streampipes.model.staticproperty.MappingPropertyNary;
import org.streampipes.model.staticproperty.MappingPropertyUnary;
import org.streampipes.sdk.helpers.CollectedStreamRequirements;
import org.streampipes.sdk.helpers.Label;

import java.util.ArrayList;
import java.util.List;

public class StreamRequirementsBuilder {

  private List<EventProperty> eventProperties;
  private List<MappingProperty> mappingProperties;

  /**
   * Creates new requirements for a data processor or a data sink.
   * @return {@link StreamRequirementsBuilder}
   */
  public static StreamRequirementsBuilder create() {
    return new StreamRequirementsBuilder();
  }

  private StreamRequirementsBuilder() {
    this.eventProperties = new ArrayList<>();
    this.mappingProperties = new ArrayList<>();
  }

  /**
   * Sets a new property requirement, e.g., a property of a specific data type or with specific semantics
   * a data stream that is connected to this preprocessing element must provide.
   * @param propertyRequirement The property requirement. Use {@link org.streampipes.sdk.helpers.EpRequirements} to
   *                            create a new requirement.
   * @return this
   */
  public StreamRequirementsBuilder requiredProperty(EventProperty propertyRequirement) {
    this.eventProperties.add(propertyRequirement);

    return this;
  }

  /**
   * Sets a new property requirement and, in addition, adds a
   * {@link org.streampipes.model.staticproperty.MappingPropertyUnary} static property to the preprocessing element
   * definition.
   * @param propertyRequirement The property requirement. Use {@link org.streampipes.sdk.helpers.EpRequirements} to
   *                            create a new requirement.
   * @param label The {@link org.streampipes.sdk.helpers.Label} that defines the mapping property.
   * @param propertyScope The {@link org.streampipes.model.schema.PropertyScope} of the requirement.
   * @return this
   */
  public StreamRequirementsBuilder requiredPropertyWithUnaryMapping(EventProperty propertyRequirement, Label label,
                                                                    PropertyScope propertyScope) {
    propertyRequirement.setRuntimeName(label.getInternalId());
    this.eventProperties.add(propertyRequirement);
    MappingPropertyUnary mp = new MappingPropertyUnary(label.getInternalId(), label
            .getInternalId(),
            label.getLabel(),
            label.getDescription());

    mp.setPropertyScope(propertyScope.name());

    this.mappingProperties.add(mp);
    return this;
  }

  /**
   * Sets a new property requirement and, in addition, adds a
   * {@link org.streampipes.model.staticproperty.MappingPropertyNary} static property to the preprocessing element
   * definition.
   * @param propertyRequirement The property requirement. Use {@link org.streampipes.sdk.helpers.EpRequirements} to
   *                            create a new requirement.
   * @param label The {@link org.streampipes.sdk.helpers.Label} that defines the mapping property.
   * @param propertyScope The {@link org.streampipes.model.schema.PropertyScope} of the requirement.
   * @return this
   */
  public StreamRequirementsBuilder requiredPropertyWithNaryMapping(EventProperty propertyRequirement, Label label, PropertyScope
          propertyScope) {
    propertyRequirement.setRuntimeName(label.getInternalId());
    this.eventProperties.add(propertyRequirement);
    MappingPropertyNary mp = new MappingPropertyNary(label.getInternalId(), label
            .getInternalId(), label.getLabel(), label.getDescription());
    mp.setPropertyScope(propertyScope.name());
    this.mappingProperties.add(mp);
    return this;
  }


  /**
   * Finishes the stream requirements definition.
   * @return an object of type {@link org.streampipes.sdk.helpers.CollectedStreamRequirements} that contains all defined
   * property requirements and static properties.
   */
  public CollectedStreamRequirements build() {
    SpDataStream stream = new SpDataStream();
    stream.setEventSchema(new EventSchema(eventProperties));

    return new CollectedStreamRequirements(stream, mappingProperties);
  }



}
