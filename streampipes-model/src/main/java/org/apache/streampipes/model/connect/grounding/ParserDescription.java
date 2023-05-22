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
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;

import java.util.List;

public class ParserDescription extends NamedStreamPipesEntity {

  private StaticPropertyGroup config;

  public ParserDescription() {
    super();
    this.config = new StaticPropertyGroup();
  }

  public ParserDescription(String uri, String name, String description) {
    super(uri, name, description);
    this.config = new StaticPropertyGroup(uri, name, description);
  }

  public ParserDescription(String uri,
                           String name,
                           String description,
                           List<StaticProperty> config) {
    super(uri, name, description);

    this.config = new StaticPropertyGroup(uri, name, description, config);
  }

  public ParserDescription(ParserDescription other) {
    super(other);
    this.config = other.getConfig();
  }

  public StaticPropertyGroup getConfig() {
    return config;
  }

  public void setConfig(StaticPropertyGroup config) {
    this.config = config;
  }

  @Override
  public String toString() {
    return String.format("FormatDescription{config='%s'}", config);
  }
}
