package org.streampipes.sdk.helpers;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.staticproperty.MappingProperty;

import java.util.ArrayList;
import java.util.List;

public class CollectedStreamRequirements {

  private SpDataStream streamRequirements;
  private List<MappingProperty> mappingProperties;

  public CollectedStreamRequirements(SpDataStream streamRequirements, List<MappingProperty> mappingProperties) {
    this.streamRequirements = streamRequirements;
    this.mappingProperties = mappingProperties;
  }

  public CollectedStreamRequirements() {
    this.mappingProperties = new ArrayList<>();
  }

  public SpDataStream getStreamRequirements() {
    return streamRequirements;
  }

  public void setStreamRequirements(SpDataStream streamRequirements) {
    this.streamRequirements = streamRequirements;
  }

  public List<MappingProperty> getMappingProperties() {
    return mappingProperties;
  }

  public void setMappingProperties(List<MappingProperty> mappingProperties) {
    this.mappingProperties = mappingProperties;
  }
}
