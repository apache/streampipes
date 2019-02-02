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

package org.streampipes.model.schema;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.quality.EventPropertyQualityDefinition;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import java.net.URI;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@RdfsClass(StreamPipes.EVENT_PROPERTY_PRIMITIVE)
@Entity
public class EventPropertyPrimitive extends EventProperty {

  private static final long serialVersionUID = 665989638281665875L;

  @RdfProperty(StreamPipes.HAS_PROPERTY_TYPE)
  private String runtimeType;

  @RdfProperty(StreamPipes.HAS_MEASUREMENT_UNIT)
  @OneToOne(cascade = {CascadeType.ALL})
  private URI measurementUnit;

  @RdfProperty(StreamPipes.HAS_VALUE_SPECIFICATION)
  @OneToOne(cascade = {CascadeType.ALL})
  private ValueSpecification valueSpecification;

  public EventPropertyPrimitive() {
    super();
  }

  public EventPropertyPrimitive(EventPropertyPrimitive other) {
    super(other);
    this.runtimeType = other.getRuntimeType();
    this.measurementUnit = other.getMeasurementUnit();
    if (other.getValueSpecification() != null) {
      this.valueSpecification = new Cloner().valueSpecification(other
              .getValueSpecification());
    }
  }

  public EventPropertyPrimitive(List<URI> subClassOf) {
    super(subClassOf);
  }

  public EventPropertyPrimitive(String runtimeType, String runtimeName,
                                String measurementUnit, List<URI> subClassOf) {
    super(runtimeName, subClassOf);
    this.runtimeType = runtimeType;
    //this.measurementUnit = measurementUnit;
  }

  public EventPropertyPrimitive(String propertyType, String propertyName,
                                String measurementUnit, List<URI> subClassOf, List<EventPropertyQualityDefinition> qualities) {
    super(propertyName, subClassOf, qualities);
    this.runtimeType = propertyType;
    //this.measurementUnit = measurementUnit;
  }

  public String getRuntimeType() {
    return runtimeType;
  }

  public void setRuntimeType(String propertyType) {
    this.runtimeType = propertyType;
  }

  public URI getMeasurementUnit() {
    return measurementUnit;
  }

  public void setMeasurementUnit(URI measurementUnit) {
    this.measurementUnit = measurementUnit;
  }

  public ValueSpecification getValueSpecification() {
    return valueSpecification;
  }

  public void setValueSpecification(ValueSpecification valueSpecification) {
    this.valueSpecification = valueSpecification;
  }

}
