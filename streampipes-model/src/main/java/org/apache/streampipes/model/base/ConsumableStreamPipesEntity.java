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

package org.apache.streampipes.model.base;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.util.Cloner;

import java.util.ArrayList;
import java.util.List;

public abstract class ConsumableStreamPipesEntity extends NamedStreamPipesEntity {

  private static final long serialVersionUID = -6617391345752016449L;

  protected List<SpDataStream> spDataStreams;

  protected List<StaticProperty> staticProperties;

  private EventGrounding supportedGrounding;

  public ConsumableStreamPipesEntity() {
    super();
    this.spDataStreams = new ArrayList<>();
    this.staticProperties = new ArrayList<>();
  }

  public ConsumableStreamPipesEntity(String uri, String name, String description, String iconUrl) {
    super(uri, name, description, iconUrl);
    this.spDataStreams = new ArrayList<>();
    this.staticProperties = new ArrayList<>();
  }

  public ConsumableStreamPipesEntity(ConsumableStreamPipesEntity other) {
    super(other);
    if (other.getSpDataStreams() != null) {
      this.spDataStreams = new Cloner().streams(other.getSpDataStreams());
    }
    this.staticProperties = new Cloner().staticProperties(other.getStaticProperties());
    if (other.getSupportedGrounding() != null) {
      this.supportedGrounding = new EventGrounding(other.getSupportedGrounding());
    }
  }

  public List<SpDataStream> getSpDataStreams() {
    return spDataStreams;
  }

  public void setSpDataStreams(List<SpDataStream> spDataStreams) {
    this.spDataStreams = spDataStreams;
  }

  public List<StaticProperty> getStaticProperties() {
    return staticProperties;
  }

  public void setStaticProperties(List<StaticProperty> staticProperties) {
    this.staticProperties = staticProperties;
  }

  public boolean addEventStream(SpDataStream spDataStream) {
    return spDataStreams.add(spDataStream);
  }

  public EventGrounding getSupportedGrounding() {
    return supportedGrounding;
  }

  public void setSupportedGrounding(EventGrounding supportedGrounding) {
    this.supportedGrounding = supportedGrounding;
  }

}
