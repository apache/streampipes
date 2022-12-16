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

package org.apache.streampipes.model.staticproperty;

import org.apache.streampipes.model.util.Cloner;

import java.util.List;

public class DomainStaticProperty extends StaticProperty {

  private static final long serialVersionUID = 1L;

  private String requiredClass;

  private List<SupportedProperty> supportedProperties;

  public DomainStaticProperty() {
    super(StaticPropertyType.DomainStaticProperty);
  }

  public DomainStaticProperty(String internalName, String label, String description,
                              List<SupportedProperty> supportedProperties) {
    super(StaticPropertyType.DomainStaticProperty, internalName, label, description);
    this.supportedProperties = supportedProperties;
  }

  public DomainStaticProperty(String internalName, String label, String description, String requiredClass,
                              List<SupportedProperty> supportedProperties) {
    this(internalName, label, description, supportedProperties);
    this.requiredClass = requiredClass;
  }

  public DomainStaticProperty(DomainStaticProperty other) {
    super(other);
    this.requiredClass = other.getRequiredClass();
    this.supportedProperties = new Cloner().supportedProperties(other.getSupportedProperties());
  }

  public String getRequiredClass() {
    return requiredClass;
  }

  public void setRequiredClass(String requiredClass) {
    this.requiredClass = requiredClass;
  }

  public List<SupportedProperty> getSupportedProperties() {
    return supportedProperties;
  }

  public void setSupportedProperties(List<SupportedProperty> supportedProperties) {
    this.supportedProperties = supportedProperties;
  }

  @Override
  public void accept(StaticPropertyVisitor visitor) {
    visitor.visit(this);
  }

}
