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

import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

@RdfsClass(StreamPipes.RUNTIME_RESOLVABLE_ONE_OF_STATIC_PROPERTY)
@Entity
public class RuntimeResolvableOneOfStaticProperty extends OneOfStaticProperty {

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.DEPENDS_ON_STATIC_PROPERTY)
  private List<String> dependsOn;

  public RuntimeResolvableOneOfStaticProperty() {
    super(StaticPropertyType.RuntimeResolvableOneOfStaticProperty);
  }

  public RuntimeResolvableOneOfStaticProperty(RuntimeResolvableOneOfStaticProperty other) {
    super(other);
    this.dependsOn = other.getDependsOn();
  }

  public RuntimeResolvableOneOfStaticProperty(String internalName, String label, String description) {
    super(StaticPropertyType.RuntimeResolvableOneOfStaticProperty, internalName, label, description);
  }

  public RuntimeResolvableOneOfStaticProperty(String internalName, String label, String description, boolean horizontalRendering) {
    super(StaticPropertyType.RuntimeResolvableOneOfStaticProperty, internalName, label, description, horizontalRendering);
  }

  public List<String> getDependsOn() {
    return dependsOn;
  }

  public void setDependsOn(List<String> dependsOn) {
    this.dependsOn = dependsOn;
  }
}
