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
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.SUPPORTED_PROPERTY)
@Entity
public class SupportedProperty extends UnnamedStreamPipesEntity {

  private static final long serialVersionUID = 1L;

  @RdfProperty(StreamPipes.REQUIRES_PROPERTY)
  private String propertyId;

  @RdfProperty(SO.ValueRequired)
  private boolean valueRequired;

  @RdfProperty(SO.Value)
  private String value;

  public SupportedProperty(SupportedProperty other) {
    super();
    this.propertyId = other.getPropertyId();
    this.valueRequired = other.isValueRequired();
    this.value = other.getValue();
  }

  public SupportedProperty() {
    super();
  }

  public SupportedProperty(String propertyId, boolean valueRequired) {
    this();
    this.propertyId = propertyId;
    this.valueRequired = valueRequired;
  }

  public String getPropertyId() {
    return propertyId;
  }

  public void setPropertyId(String propertyId) {
    this.propertyId = propertyId;
  }

  public boolean isValueRequired() {
    return valueRequired;
  }

  public void setValueRequired(boolean valueRequired) {
    this.valueRequired = valueRequired;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

}
