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
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.util.Cloner;

import java.util.ArrayList;
import java.util.List;

public class FormatDescription extends NamedStreamPipesEntity {

  private List<StaticProperty> config;

  private String formatType = "";

  public FormatDescription() {
    super();
    this.config = new ArrayList<>();
  }

  public FormatDescription(String uri, String name, String description) {
    super(uri, name, description);
    this.config = new ArrayList<>();
  }

  public FormatDescription(String uri, String name, String description, List<StaticProperty> config,
                           String formatType) {
    super(uri, name, description);
    this.config = config;
    this.formatType = formatType;
  }

  public FormatDescription(FormatDescription other) {
    super(other);
    this.config = new Cloner().staticProperties(other.getConfig());
    this.formatType = other.getFormatType();
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

  public String getFormatType() {
    return formatType;
  }

  public void setFormatType(String formatType) {
    this.formatType = formatType;
  }

  @Override
  public String toString() {
    return String.format("FormatDescription{formatType='%s', config='%s'}", formatType, config);
  }
}
