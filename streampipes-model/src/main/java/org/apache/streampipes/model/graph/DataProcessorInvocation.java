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

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.model.util.ElementIdGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataProcessorInvocation extends InvocableStreamPipesEntity implements Serializable {

  private static final long serialVersionUID = 865870355944824186L;

  private SpDataStream outputStream;

  private List<OutputStrategy> outputStrategies;

  private String pathName;

  private List<String> category;

  public DataProcessorInvocation(DataProcessorDescription other) {
    super();
    this.setName(other.getName());
    this.setDescription(other.getDescription());
    this.setIconUrl(other.getIconUrl());
    this.setInputStreams(other.getSpDataStreams());
    this.setSupportedGrounding(other.getSupportedGrounding());
    this.setStaticProperties(other.getStaticProperties());
    this.setOutputStrategies(other.getOutputStrategies());
    this.setBelongsTo(other.getElementId());
    this.category = other.getCategory();
    this.setStreamRequirements(other.getSpDataStreams());
    this.setAppId(other.getAppId());
    this.setIncludesAssets(other.isIncludesAssets());
    this.setIncludedAssets(other.getIncludedAssets());
    this.setElementId(ElementIdGenerator.makeElementId(this));
  }

  public DataProcessorInvocation(DataProcessorInvocation other) {
    super(other);
    this.outputStrategies = new Cloner().strategies(other.getOutputStrategies());
    if (other.getOutputStream() != null) {
      this.outputStream = new Cloner().stream(other.getOutputStream());
    }
    this.pathName = other.getPathName();
    this.category = new Cloner().epaTypes(other.getCategory());
  }

  public DataProcessorInvocation(DataProcessorDescription sepa, String domId) {
    this(sepa);
    this.dom = domId;
  }

  public DataProcessorInvocation() {
    super();
    inputStreams = new ArrayList<>();
  }

  public DataProcessorInvocation(String uri, String name, String description, String iconUrl, String pathName,
                                 List<SpDataStream> spDataStreams, List<StaticProperty> staticProperties) {
    super(uri, name, description, iconUrl);
    this.pathName = pathName;
    this.inputStreams = spDataStreams;
    this.staticProperties = staticProperties;
  }

  public DataProcessorInvocation(String uri, String name, String description, String iconUrl, String pathName) {
    super(uri, name, description, iconUrl);
    this.pathName = pathName;
    inputStreams = new ArrayList<>();
    staticProperties = new ArrayList<>();
  }

  public boolean addInputStream(SpDataStream spDataStream) {
    return inputStreams.add(spDataStream);
  }


  public String getPathName() {
    return pathName;
  }

  public void setPathName(String pathName) {
    this.pathName = pathName;
  }

  public SpDataStream getOutputStream() {
    return outputStream;
  }

  public void setOutputStream(SpDataStream outputStream) {
    this.outputStream = outputStream;
  }

  public List<OutputStrategy> getOutputStrategies() {
    return outputStrategies;
  }

  public void setOutputStrategies(List<OutputStrategy> outputStrategies) {
    this.outputStrategies = outputStrategies;
  }

  public List<String> getCategory() {
    return category;
  }

  public void setCategory(List<String> category) {
    this.category = category;
  }

}
