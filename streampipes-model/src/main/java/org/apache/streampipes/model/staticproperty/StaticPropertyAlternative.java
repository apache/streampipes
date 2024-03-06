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
import org.apache.streampipes.model.util.ElementIdGenerator;

import java.util.Objects;

public class StaticPropertyAlternative extends StaticProperty {

  private String elementId;
  private boolean selected;

  private StaticProperty staticProperty;

  public StaticPropertyAlternative() {
    super(StaticPropertyType.StaticPropertyAlternative);
    this.elementId = ElementIdGenerator.makeElementId(StaticPropertyAlternative.class);
  }

  public StaticPropertyAlternative(String internalName,
                                   String label,
                                   String description) {
    super(StaticPropertyType.StaticPropertyAlternative, internalName, label, description);
    this.elementId = ElementIdGenerator.makeElementId(StaticPropertyAlternative.class);
  }

  public StaticPropertyAlternative(StaticPropertyAlternative other) {
    super(other);
    this.elementId = other.getElementId();
    this.selected = other.getSelected();
    if (other.getStaticProperty() != null) {
      this.staticProperty = new Cloner().staticProperty(other.getStaticProperty());
    }
  }

  public String getElementId() {
    return elementId;
  }

  public void setElementId(String elementId) {
    this.elementId = elementId;
  }

  public Boolean getSelected() {
    return selected;
  }

  public void setSelected(Boolean selected) {
    this.selected = selected;
  }

  public StaticProperty getStaticProperty() {
    return staticProperty;
  }

  public void setStaticProperty(StaticProperty staticProperty) {
    this.staticProperty = staticProperty;
  }

  @Override
  public void accept(StaticPropertyVisitor visitor) {
    visitor.visit(this);
  }

  /**
   * equals() excludes elementId
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof StaticPropertyAlternative that)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    if (selected != that.selected) {
      return false;
    }

    return Objects.equals(staticProperty, that.staticProperty);
  }

  /**
   * hashCode() excludes elementId
   */
  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (selected ? 1 : 0);
    result = 31 * result + (staticProperty != null ? staticProperty.hashCode() : 0);
    return result;
  }
}
