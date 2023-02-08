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

package org.apache.streampipes.sdk.builder;

import org.apache.streampipes.model.schema.Enumeration;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.schema.QuantitativeValue;
import org.apache.streampipes.sdk.utils.Datatypes;

import java.net.URI;
import java.util.Collections;
import java.util.List;

public class PrimitivePropertyBuilder {

  private EventPropertyPrimitive eventProperty;

  private PrimitivePropertyBuilder(Datatypes datatype, String runtimeName) {
    this.eventProperty = new EventPropertyPrimitive();
    this.eventProperty.setRuntimeType(datatype.toString());
    this.eventProperty.setRuntimeName(runtimeName);
  }

  /**
   * A builder class helping to define advanced primitive properties. For simple property definitions, you can also
   * use {@link org.apache.streampipes.sdk.helpers.EpProperties}.
   *
   * @param datatype    The primitive {@link org.apache.streampipes.sdk.utils.Datatypes} definition of the new property.
   * @param runtimeName The name of the property at runtime (e.g., the field name of the JSON primitive.
   * @return this
   */
  public static PrimitivePropertyBuilder create(Datatypes datatype, String runtimeName) {
    return new PrimitivePropertyBuilder(datatype, runtimeName);
  }

  /**
   * Specifies the semantics of the property (e.g., whether a double value stands for a latitude coordinate).
   *
   * @param domainProperty The domain property as a String. The domain property should reflect an URI. Use some
   *                       existing vocabulary from {@link org.apache.streampipes.vocabulary} or create your own.
   * @return
   */
  public PrimitivePropertyBuilder domainProperty(String domainProperty) {
    this.eventProperty.setDomainProperties(Collections.singletonList(URI.create(domainProperty)));
    return this;
  }

  /**
   * Defines the measurement unit (e.g., tons) of the event property.
   *
   * @param measurementUnit The measurement unit as a URI from a vocabulary (e.g., QUDT).
   * @return
   */
  public PrimitivePropertyBuilder measurementUnit(URI measurementUnit) {
    this.eventProperty.setMeasurementUnit(measurementUnit);
    return this;
  }

  /**
   * Defines the value range. The data type of the event property must be a number.
   *
   * @param min  The minimum value the property can have at runtime.
   * @param max  The maximum value the property can have at runtime.
   * @param step The expected granularity the property has at runtime.
   * @return this
   */
  public PrimitivePropertyBuilder valueSpecification(Float min, Float max, Float step) {
    this.eventProperty.setValueSpecification(new QuantitativeValue(min, max, step));
    return this;
  }

  /**
   * Defines the value range in form of an enumeration. The data type of the event property must be of type String
   * or Number.
   *
   * @param label         A human-readable label describing this enumeration.
   * @param description   A human-readable description of the enumeration.
   * @param allowedValues A list of allowed values of the event property at runtime.
   * @return this
   */
  public PrimitivePropertyBuilder valueSpecification(String label, String description, List<String> allowedValues) {
    this.eventProperty.setValueSpecification(new Enumeration(label, description, allowedValues));
    return this;
  }

  /**
   * Assigns a human-readable label to the event property. The label is used in the StreamPipes UI for better
   * explaining  users the meaning of the property.
   *
   * @param label
   * @return this
   */
  public PrimitivePropertyBuilder label(String label) {
    this.eventProperty.setLabel(label);
    return this;
  }

  /**
   * Assigns a human-readable description to the event property. The description is used in the StreamPipes UI for
   * better explaining users the meaning of the property.
   *
   * @param description
   * @return this
   */
  public PrimitivePropertyBuilder description(String description) {
    this.eventProperty.setDescription(description);
    return this;
  }

  /**
   * Assigns a property scope to the event property.
   *
   * @param propertyScope The {@link org.apache.streampipes.model.schema.PropertyScope}.
   * @return this
   */
  public PrimitivePropertyBuilder scope(PropertyScope propertyScope) {
    this.eventProperty.setPropertyScope(propertyScope.name());
    return this;
  }


  public EventPropertyPrimitive build() {
    return this.eventProperty;
  }

}
