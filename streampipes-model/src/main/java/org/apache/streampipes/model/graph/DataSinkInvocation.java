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

package org.apache.streampipes.model.graph;

import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.util.ElementIdGenerator;

import java.util.ArrayList;
import java.util.List;

public class DataSinkInvocation extends InvocableStreamPipesEntity {

  private static final long serialVersionUID = -2345635798917416757L;

  private List<String> category;

  public DataSinkInvocation(DataSinkInvocation sec) {
    super(sec);
    this.category = sec.getCategory();
  }

  public DataSinkInvocation(DataSinkDescription other) {
    super();
    this.setName(other.getName());
    this.setDescription(other.getDescription());
    this.setIconUrl(other.getIconUrl());
    this.setInputStreams(other.getSpDataStreams());
    this.setSupportedGrounding(other.getSupportedGrounding());
    this.setStaticProperties(other.getStaticProperties());
    this.setBelongsTo(other.getElementId());
    this.category = other.getCategory();
    this.setStreamRequirements(other.getSpDataStreams());
    this.setAppId(other.getAppId());
    this.setIncludesAssets(other.isIncludesAssets());
    this.setElementId(ElementIdGenerator.makeElementId(this));
    this.setIncludedAssets(other.getIncludedAssets());
  }

  public DataSinkInvocation(DataSinkDescription sec, String domId) {
    this(sec);
    this.setDom(domId);
  }

  public DataSinkInvocation() {
    super();
    inputStreams = new ArrayList<>();
  }

  public List<StaticProperty> getStaticProperties() {
    return staticProperties;
  }

  public void setStaticProperties(List<StaticProperty> staticProperties) {
    this.staticProperties = staticProperties;
  }

  public List<String> getCategory() {
    return category;
  }

  public void setCategory(List<String> category) {
    this.category = category;
  }

}
