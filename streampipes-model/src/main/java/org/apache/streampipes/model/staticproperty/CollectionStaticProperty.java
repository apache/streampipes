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

import java.util.ArrayList;
import java.util.List;

public class CollectionStaticProperty extends StaticProperty {

  private static final long serialVersionUID = 1L;

  private StaticProperty staticPropertyTemplate;

  private List<StaticProperty> members;

  private String memberType;

  public CollectionStaticProperty() {
    super(StaticPropertyType.CollectionStaticProperty);
    this.members = new ArrayList<>();
  }

  public CollectionStaticProperty(String internalName, String label, String description, List<StaticProperty> members,
                                  String memberType) {
    super(StaticPropertyType.CollectionStaticProperty, internalName, label, description);
    this.members = members;
    this.memberType = memberType;
  }

  public CollectionStaticProperty(String internalName, String label, String description,
                                  StaticProperty propertyTemplate) {
    super(StaticPropertyType.CollectionStaticProperty, internalName, label, description);
    this.staticPropertyTemplate = propertyTemplate;
  }

  public CollectionStaticProperty(CollectionStaticProperty other) {
    super(other);
    this.members = new Cloner().staticProperties(other.getMembers());
    if (other.getStaticPropertyTemplate() != null) {
      this.staticPropertyTemplate = new Cloner()
          .staticProperty(other.getStaticPropertyTemplate());
    }
    this.memberType = other.getMemberType();
  }

  public List<StaticProperty> getMembers() {
    return members;
  }

  public void setMembers(List<StaticProperty> members) {
    this.members = members;
  }

  public String getMemberType() {
    return memberType;
  }

  public void setMemberType(String memberType) {
    this.memberType = memberType;
  }

  public StaticProperty getStaticPropertyTemplate() {
    return staticPropertyTemplate;
  }

  public void setStaticPropertyTemplate(StaticProperty staticPropertyTemplate) {
    this.staticPropertyTemplate = staticPropertyTemplate;
  }

  @Override
  public void accept(StaticPropertyVisitor visitor) {
    visitor.visit(this);
  }
}
