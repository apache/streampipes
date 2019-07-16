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
import org.streampipes.vocabulary.StreamPipes;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass(StreamPipes.STATIC_PROPERTY_ALTERNATIVES)
@Entity
public class StaticPropertyAlternatives extends StaticProperty {

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_STATIC_PROPERTY_ALTERNATIVE)
  private List<StaticPropertyAlternative> alternatives;

  public StaticPropertyAlternatives() {
    super(StaticPropertyType.StaticPropertyAlternatives);
  }

  public StaticPropertyAlternatives(StaticPropertyAlternatives other) {
    super(other);
    this.alternatives = other
            .getAlternatives()
            .stream()
            .map(StaticPropertyAlternative::new)
            .collect(Collectors.toList());
  }

  public StaticPropertyAlternatives(String internalName, String label, String description) {
    super(StaticPropertyType.StaticPropertyAlternatives, internalName, label, description);
  }

  public List<StaticPropertyAlternative> getAlternatives() {
    return alternatives;
  }

  public void setAlternatives(List<StaticPropertyAlternative> alternatives) {
    this.alternatives = alternatives;
  }
}
