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

public class AnyStaticProperty extends SelectionStaticProperty {

  private static final long serialVersionUID = -7046019539598560494L;

  public AnyStaticProperty() {
    super(StaticPropertyType.AnyStaticProperty);
  }

  public AnyStaticProperty(AnyStaticProperty other) {
    super(other);
  }

  public AnyStaticProperty(StaticPropertyType staticPropertyType) {
    super(staticPropertyType);
  }

  public AnyStaticProperty(StaticPropertyType staticPropertyType, String internalName,
                           String label, String description) {
    super(staticPropertyType, internalName, label, description);
  }

  public AnyStaticProperty(StaticPropertyType staticPropertyType, String internalName, String label, String
      description, boolean horizontalRendering) {
    super(staticPropertyType, internalName, label, description, horizontalRendering);
  }

  public AnyStaticProperty(String internalName, String label, String description) {
    super(StaticPropertyType.AnyStaticProperty, internalName, label, description);
  }

  public AnyStaticProperty(String internalName, String label, String description, boolean horizontalRendering) {
    super(StaticPropertyType.AnyStaticProperty, internalName, label, description, horizontalRendering);
  }

  @Override
  public void accept(StaticPropertyVisitor visitor) {
    visitor.visit(this);
  }

}
