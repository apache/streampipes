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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass(StreamPipes.MAPPING_PROPERTY_NARY)
@Entity
public class MappingPropertyNary extends MappingProperty {

  private static final long serialVersionUID = 7570213252902343160L;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.MAPS_TO)
  private List<String> selectedProperties;

  public MappingPropertyNary() {
    super(StaticPropertyType.MappingPropertyNary);
    this.selectedProperties = new ArrayList<>();
  }

  public MappingPropertyNary(MappingPropertyNary other) {
    super(other);
    this.selectedProperties = other.getSelectedProperties();
  }

  public MappingPropertyNary(String requirementSelector, String internalName, String label, String
          description) {
    super(StaticPropertyType.MappingPropertyNary, requirementSelector, internalName, label, description);
    this.selectedProperties = new ArrayList<>();
  }

  public MappingPropertyNary(String internalName, String label, String description) {
    super(StaticPropertyType.MappingPropertyNary, internalName, label, description);
    this.selectedProperties = new ArrayList<>();
  }

  public List<String> getSelectedProperties() {
    return selectedProperties;
  }

  public void setSelectedProperties(List<String> selectedProperties) {
    this.selectedProperties = selectedProperties;
  }

}
