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

import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.vocabulary.StreamPipes;

import java.net.URI;
import java.util.Arrays;
import java.util.stream.Collectors;

public class PropertyRequirementsBuilder {

  private EventPropertyPrimitive propertyReq;

  private PropertyRequirementsBuilder() {
    this.propertyReq = new EventPropertyPrimitive();
  }

  private PropertyRequirementsBuilder(Datatypes propertyDatatype) {
    this.propertyReq = new EventPropertyPrimitive();
    this.propertyReq.setRuntimeType(propertyDatatype.toString());
  }

  /**
   * Creates new requirements for a data processor or a data sink at a property level. A matching event property
   * needs to provide all requirements assigned by this class.
   *
   * @return {@link PropertyRequirementsBuilder}
   */
  public static PropertyRequirementsBuilder create(Datatypes propertyDatatype) {
    return new PropertyRequirementsBuilder(propertyDatatype);
  }

  public static PropertyRequirementsBuilder create() {
    return new PropertyRequirementsBuilder();
  }

  public PropertyRequirementsBuilder datatype(Datatypes propertyDatatype) {
    this.propertyReq.setRuntimeType(propertyDatatype.toString());
    return this;
  }

  public PropertyRequirementsBuilder domainPropertyReq(String... domainProperties) {
    this.propertyReq.setDomainProperties(Arrays
        .stream(domainProperties)
        .map(URI::create)
        .collect(Collectors.toList()));

    return this;
  }

  public PropertyRequirementsBuilder measurementUnitReq(String measurementUnit) {
    this.propertyReq.setMeasurementUnit(URI.create(measurementUnit));
    return this;
  }

  public PropertyRequirementsBuilder measurementUnitPresence() {
    this.propertyReq.setMeasurementUnit(URI.create(StreamPipes.ANYTHING));

    return this;
  }

  public EventPropertyPrimitive build() {
    return this.propertyReq;
  }
}
