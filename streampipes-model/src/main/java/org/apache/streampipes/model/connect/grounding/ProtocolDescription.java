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

package org.apache.streampipes.model.connect.grounding;

import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.util.Cloner;

import java.util.ArrayList;
import java.util.List;

@TsModel
@Deprecated(since = "0.93.0", forRemoval = true)
public class ProtocolDescription extends NamedStreamPipesEntity {

  @Deprecated
  String sourceType;

  List<StaticProperty> config;

  private List<String> category;

  public ProtocolDescription() {
    this.config = new ArrayList<>();
  }

  public ProtocolDescription(String uri, String name, String description) {
    super(uri, name, description);
    this.config = new ArrayList<>();
    this.category = new ArrayList<>();
  }

  public ProtocolDescription(String uri, String name, String description, List<StaticProperty> config) {
    super(uri, name, description);
    this.config = config;
    this.category = new ArrayList<>();
  }

  public ProtocolDescription(ProtocolDescription other) {
    super(other);

    this.config = new Cloner().staticProperties(other.getConfig());
    if (other.getCategory() != null) {
      this.category = new Cloner().epaTypes(other.getCategory());
    }
  }

  public void addConfig(StaticProperty sp) {
    this.config.add(sp);
  }

  public List<StaticProperty> getConfig() {
    return config;
  }

  public void setConfig(List<StaticProperty> config) {
    this.config = config;
  }

  public String getSourceType() {
    return sourceType;
  }

  public void setSourceType(String sourceType) {
    this.sourceType = sourceType;
  }

  public List<String> getCategory() {
    return category;
  }

  public void setCategory(List<String> category) {
    this.category = category;
  }
}
