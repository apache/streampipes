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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class StreamRequirementsBuilder {

  private List<EventProperty> eventProperties;
  private List<MappingProperty> mappingProperties;

  public static StreamRequirementsBuilder create() {
    return new StreamRequirementsBuilder();
  }

  private StreamRequirementsBuilder() {
    this.eventProperties = new ArrayList<>();
    this.mappingProperties = new ArrayList<>();
  }

  public StreamRequirementsBuilder requiredProperty(EventProperty propertyRequirement) {
    this.eventProperties.add(propertyRequirement);

    return this;
  }

  public StreamRequirementsBuilder requiredPropertyWithUnaryMapping(EventProperty propertyRequirement, Label label,
                                                                    PropertyScope propertyScope) {
    this.eventProperties.add(propertyRequirement);
    MappingPropertyUnary mp = new MappingPropertyUnary(URI.create(propertyRequirement.getElementId()), label
            .getInternalId(),
            label.getLabel(),
            label.getDescription());

    mp.setPropertyScope(propertyScope.name());

    this.mappingProperties.add(mp);
    return this;
  }

  public StreamRequirementsBuilder requiredPropertyWithNaryMapping(EventProperty propertyRequirement, Label label, PropertyScope
          propertyScope) {
    this.eventProperties.add(propertyRequirement);
    MappingPropertyNary mp = new MappingPropertyNary(URI.create(propertyRequirement.getElementId()), label
            .getInternalId(), label.getLabel(), label.getDescription());
    mp.setPropertyScope(propertyScope.name());
    this.mappingProperties.add(mp);
    return this;
  }


  public CollectedStreamRequirements build() {
    SpDataStream stream = new SpDataStream();
    stream.setEventSchema(new EventSchema(eventProperties));

    return new CollectedStreamRequirements(stream, mappingProperties);
  }



}
