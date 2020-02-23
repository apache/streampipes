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
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.vocabulary.StreamPipes;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass(StreamPipes.STATIC_PROPERTY_GROUP)
@Entity
public class StaticPropertyGroup extends StaticProperty {

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_STATIC_PROPERTY)
  private List<StaticProperty> staticProperties;

  @RdfProperty(StreamPipes.SHOW_LABEL)
  private Boolean showLabel;

  @RdfProperty(StreamPipes.IS_HORIZONTAL_RENDERING)
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

  public void setShowLabel(Boolean showLabel) {
    this.showLabel = showLabel;
  }

  public StaticPropertyGroup(String internalName, String label, String description) {
    super(StaticPropertyType.StaticPropertyGroup, internalName, label, description);
  }

  public StaticPropertyGroup(String internalName, String label, String description, boolean horizontalRendering) {
    super(StaticPropertyType.StaticPropertyGroup, internalName, label, description);
    this.horizontalRendering = horizontalRendering;
  }

  public StaticPropertyGroup(String internalName, String label, String description, List<StaticProperty> staticProperties) {
    this(internalName, label, description);
    this.staticProperties = staticProperties;
  }

  public List<StaticProperty> getStaticProperties() {
    return staticProperties;
  }

  public void setStaticProperties(List<StaticProperty> staticProperties) {
    this.staticProperties = staticProperties;
  }

  public void setHorizontalRendering(boolean horizontalRendering) {
    this.horizontalRendering = horizontalRendering;
  }

  public Boolean getShowLabel() {
    return showLabel;
  }

  public boolean isHorizontalRendering() {
    return horizontalRendering;
  }
}
