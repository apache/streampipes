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
package org.streampipes.model.staticproperty;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.StreamPipes;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass(StreamPipes.RUNTIME_RESOLVABLE_ANY_STATIC_PROPERTY)
@Entity
public class RuntimeResolvableAnyStaticProperty extends OneOfStaticProperty {

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.DEPENDS_ON_STATIC_PROPERTY)
  private List<String> dependsOn;

    public RuntimeResolvableAnyStaticProperty() {
      super(StaticPropertyType.RuntimeResolvableAnyStaticProperty);
    }

  public RuntimeResolvableAnyStaticProperty(RuntimeResolvableOneOfStaticProperty other) {
    super(other);
    this.dependsOn = other.getDependsOn();
  }

    public RuntimeResolvableAnyStaticProperty(String internalName, String label, String description) {
      super(StaticPropertyType.RuntimeResolvableAnyStaticProperty, internalName, label, description);
    }
}
