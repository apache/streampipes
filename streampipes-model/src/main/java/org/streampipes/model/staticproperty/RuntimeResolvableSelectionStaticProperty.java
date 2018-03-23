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

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

@RdfsClass(StreamPipes.RUNTIME_RESOLVABLE_SELECTION_STATIC_PROPERTY)
@MappedSuperclass
@Entity
public abstract class RuntimeResolvableSelectionStaticProperty extends SelectionStaticProperty {

  @RdfProperty(StreamPipes.HAS_LINKED_MAPPING_PROPERTY_ID)
  private String linkedMappingPropertyId;

  public RuntimeResolvableSelectionStaticProperty(StaticPropertyType staticPropertyType) {
    super(staticPropertyType);
  }

  public RuntimeResolvableSelectionStaticProperty(RuntimeResolvableSelectionStaticProperty other) {
    super(other);
    if (other.getLinkedMappingPropertyId() != null) {
      this.linkedMappingPropertyId = other.getLinkedMappingPropertyId();
    }
  }

  public RuntimeResolvableSelectionStaticProperty(StaticPropertyType staticPropertyType, String internalName, String label, String
          description) {
    super(staticPropertyType, internalName, label, description);
  }

  public String getLinkedMappingPropertyId() {
    return linkedMappingPropertyId;
  }

  public void setLinkedMappingPropertyId(String linkedMappingPropertyId) {
    this.linkedMappingPropertyId = linkedMappingPropertyId;
  }
}
