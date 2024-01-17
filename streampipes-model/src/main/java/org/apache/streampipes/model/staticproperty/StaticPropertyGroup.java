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
import java.util.Objects;

public class StaticPropertyGroup extends StaticProperty {

  private List<StaticProperty> staticProperties;

  private Boolean showLabel;

  private boolean horizontalRendering;

  public StaticPropertyGroup() {
    super(StaticPropertyType.StaticPropertyGroup);
  }

  public StaticPropertyGroup(StaticPropertyGroup other) {
    super(other);
    this.staticProperties = new Cloner().staticProperties(other.getStaticProperties());
    this.showLabel = other.showLabel;
    this.horizontalRendering = other.horizontalRendering;
  }

  public StaticPropertyGroup(String internalName, String label, String description) {
    super(StaticPropertyType.StaticPropertyGroup, internalName, label, description);
  }

  public StaticPropertyGroup(String internalName, String label, String description, boolean horizontalRendering) {
    super(StaticPropertyType.StaticPropertyGroup, internalName, label, description);
    this.horizontalRendering = horizontalRendering;
  }

  public StaticPropertyGroup(String internalName, String label, String description,
                             List<StaticProperty> staticProperties) {
    this(internalName, label, description);
    this.staticProperties = staticProperties;
  }

  public List<StaticProperty> getStaticProperties() {
    return staticProperties;
  }

  public void setStaticProperties(List<StaticProperty> staticProperties) {
    this.staticProperties = staticProperties;
  }

  public Boolean getShowLabel() {
    return showLabel;
  }

  public void setShowLabel(Boolean showLabel) {
    this.showLabel = showLabel;
  }

  public boolean isHorizontalRendering() {
    return horizontalRendering;
  }

  public void setHorizontalRendering(boolean horizontalRendering) {
    this.horizontalRendering = horizontalRendering;
  }

  @Override
  public void accept(StaticPropertyVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof StaticPropertyGroup that)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    if (horizontalRendering != that.horizontalRendering) {
      return false;
    }

    if (!(this.staticProperties.size() == that.staticProperties.size())) {
      return false;
    }

    for (var i = 0; i < staticProperties.size(); i++){
      if (!staticProperties.get(i).equals(that.staticProperties.get(i))){
        return false;
      }
    }
    return Objects.equals(showLabel, that.showLabel);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (staticProperties != null ? staticProperties.hashCode() : 0);
    result = 31 * result + (showLabel != null ? showLabel.hashCode() : 0);
    result = 31 * result + (horizontalRendering ? 1 : 0);
    return result;
  }
}
