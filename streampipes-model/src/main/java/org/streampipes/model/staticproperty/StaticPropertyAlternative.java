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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@RdfsClass(StreamPipes.STATIC_PROPERTY_ALTERNATIVE)
@Entity
public class StaticPropertyAlternative extends StaticProperty {

  @OneToOne(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.IS_SELECTED)
  private Boolean selected;

  @OneToOne(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_STATIC_PROPERTY)
  private StaticProperty staticProperty;

  public StaticPropertyAlternative() {
    super(StaticPropertyType.StaticPropertyAlternative);
  }

  public StaticPropertyAlternative(String internalName,
                             String label, String description) {
    super(StaticPropertyType.StaticPropertyAlternative, internalName, label, description);
  }

  public StaticPropertyAlternative(StaticPropertyAlternative other) {
    super(other);
    this.selected = other.getSelected();
    this.staticProperty = new Cloner().staticProperty(other.getStaticProperty());
  }

  public Boolean getSelected() {
    return selected;
  }

  public void setSelected(Boolean selected) {
    this.selected = selected;
  }

  public StaticProperty getStaticProperty() {
    return staticProperty;
  }

  public void setStaticProperty(StaticProperty staticProperty) {
    this.staticProperty = staticProperty;
  }
}
