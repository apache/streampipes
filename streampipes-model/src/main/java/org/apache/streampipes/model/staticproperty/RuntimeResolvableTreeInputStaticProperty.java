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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RuntimeResolvableTreeInputStaticProperty extends StaticProperty {

  private List<String> dependsOn;
  private List<TreeInputNode> nodes;

  public RuntimeResolvableTreeInputStaticProperty() {
    super(StaticPropertyType.RuntimeResolvableTreeInputStaticProperty);
    this.dependsOn = new ArrayList<>();
    this.nodes = new ArrayList<>();
  }

  public RuntimeResolvableTreeInputStaticProperty(String internalName,
                                                  String label,
                                                  String description) {
    super(StaticPropertyType.RuntimeResolvableTreeInputStaticProperty, internalName, label, description);
    this.dependsOn = new ArrayList<>();
    this.nodes = new ArrayList<>();
  }

  public RuntimeResolvableTreeInputStaticProperty(RuntimeResolvableTreeInputStaticProperty other) {
    super(other);
    this.dependsOn = other.getDependsOn();
    this.nodes = other.getNodes().stream().map(TreeInputNode::new).collect(Collectors.toList());
  }

  @Override
  public void accept(StaticPropertyVisitor visitor) {
    visitor.visit(this);
  }

  public List<TreeInputNode> getNodes() {
    return nodes;
  }

  public void setNodes(List<TreeInputNode> nodes) {
    this.nodes = nodes;
  }

  public List<String> getDependsOn() {
    return dependsOn;
  }

  public void setDependsOn(List<String> dependsOn) {
    this.dependsOn = dependsOn;
  }
}
