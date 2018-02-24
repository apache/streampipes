/*
Copyright 2018 FZI Forschungszentrum Informatik

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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

@RdfsClass(StreamPipes.SELECTION_STATIC_PROPERTY)
@MappedSuperclass
@Entity
public abstract class SelectionStaticProperty extends StaticProperty {

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_OPTION)
  private List<Option> options;

  public SelectionStaticProperty(StaticPropertyType staticPropertyType) {
    super(staticPropertyType);
    this.options = new ArrayList<>();
  }

  public SelectionStaticProperty(SelectionStaticProperty other) {
    super(other);
    this.options = new Cloner().options(other.getOptions());
  }

  public SelectionStaticProperty(StaticPropertyType staticPropertyType, String internalName, String label, String
          description) {
    super(staticPropertyType, internalName, label, description);
    this.options = new ArrayList<>();
  }

  public List<Option> getOptions() {
    return options;
  }

  public void setOptions(List<Option> options) {
    this.options = options;
  }

  public boolean addOption(Option option)
  {
    return options.add(option);
  }
}
