/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.model.staticproperty;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass(StreamPipes.STATIC_PROPERTY_GROUP)
@Entity
public class StaticPropertyGroup extends StaticProperty {

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_STATIC_PROPERTY)
  private List<StaticProperty> staticProperties;

  @RdfProperty(StreamPipes.SHOW_LABEL)
  private Boolean showLabel;

  public StaticPropertyGroup() {
    super(StaticPropertyType.StaticPropertyGroup);
  }

  public StaticPropertyGroup(StaticPropertyGroup other) {
    super(other);
    this.staticProperties = new Cloner().staticProperties(other.getStaticProperties());
    this.showLabel = other.showLabel;
  }

  public StaticPropertyGroup(String internalName, String label, String description) {
    super(StaticPropertyType.StaticPropertyGroup, internalName, label, description);
  }

  public StaticPropertyGroup(String internalName, String label, String description, List<StaticProperty> staticProperties) {
    this(internalName, label, description);
    this.staticProperties = staticProperties;
  }

  public List<StaticProperty> getStaticProperties() {
    return staticProperties;
  }

  public void setStaticProperties(List<StaticProperty> staticProperties) {
    this.staticProperties = staticProperties;
  }
}
