/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.model.schema;

import org.apache.streampipes.model.util.Cloner;

import java.net.URI;
import java.util.List;
import java.util.Objects;

public class EventPropertyPrimitive extends EventProperty {

  private static final long serialVersionUID = 665989638281665875L;

  private String runtimeType;

  private URI measurementUnit;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    EventPropertyPrimitive that = (EventPropertyPrimitive) o;
    return Objects.equals(runtimeType, that.runtimeType)
           && Objects.equals(measurementUnit, that.measurementUnit)
           && Objects.equals(valueSpecification, that.valueSpecification);
  }

  @Override
  public int hashCode() {
    return Objects.hash(runtimeType, measurementUnit, valueSpecification);
  }

  @Override
  public String toString() {
    return "EventPropertyPrimitive{"
           + "runtimeType='" + runtimeType + '\''
           + ", measurementUnit=" + measurementUnit
           + ", valueSpecification=" + valueSpecification
           + '}';
  }
}
