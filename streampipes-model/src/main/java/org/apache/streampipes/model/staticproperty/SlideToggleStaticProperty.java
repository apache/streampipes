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

public class SlideToggleStaticProperty extends StaticProperty {

  private boolean selected;
  private boolean defaultValue;

  public SlideToggleStaticProperty() {
    super(StaticPropertyType.SlideToggleStaticProperty);
  }

  public SlideToggleStaticProperty(String internalName,
                                   String label,
                                   String description,
                                   boolean defaultValue) {
    super(StaticPropertyType.SlideToggleStaticProperty, internalName, label, description);
    this.defaultValue = defaultValue;
  }

  public SlideToggleStaticProperty(SlideToggleStaticProperty other) {
    super(other);
    this.selected = other.isSelected();
    this.defaultValue = other.isDefaultValue();
  }


  @Override
  public void accept(StaticPropertyVisitor visitor) {
    visitor.visit(this);
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public boolean isDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(boolean defaultValue) {
    this.defaultValue = defaultValue;
  }
}
