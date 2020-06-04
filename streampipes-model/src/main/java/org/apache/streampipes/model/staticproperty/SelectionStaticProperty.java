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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@RdfsClass(StreamPipes.SELECTION_STATIC_PROPERTY)
@MappedSuperclass
@Entity
@JsonSubTypes({
        @JsonSubTypes.Type(AnyStaticProperty.class),
        @JsonSubTypes.Type(OneOfStaticProperty.class)
})
public abstract class SelectionStaticProperty extends StaticProperty {

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_OPTION)
  private List<Option> options;

  @RdfProperty(StreamPipes.IS_HORIZONTAL_RENDERING)
  private boolean horizontalRendering;


  public SelectionStaticProperty(StaticPropertyType staticPropertyType) {
    super(staticPropertyType);
    this.options = new ArrayList<>();
  }

  public SelectionStaticProperty(SelectionStaticProperty other) {
    super(other);
    this.options = new Cloner().options(other.getOptions());
    this.horizontalRendering = other.horizontalRendering;
  }

  public SelectionStaticProperty(StaticPropertyType staticPropertyType, String internalName, String label, String
          description) {
    super(staticPropertyType, internalName, label, description);
    this.options = new ArrayList<>();
  }

  public SelectionStaticProperty(StaticPropertyType staticPropertyType, String internalName, String label, String
          description, boolean horizontalRendering) {
    super(staticPropertyType, internalName, label, description);
    this.options = new ArrayList<>();
    this.horizontalRendering = horizontalRendering;
  }

  public List<Option> getOptions() {
    return options;
  }

  public void setOptions(List<Option> options) {
    this.options = options;
  }

  public boolean addOption(Option option)
  {
    return options.add(option);
  }

  public void setHorizontalRendering(boolean horizontalRendering) {
    this.horizontalRendering = horizontalRendering;
  }

  public boolean isHorizontalRendering() {
    return horizontalRendering;
  }
}
