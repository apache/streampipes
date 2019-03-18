/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.model.graph;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.vocabulary.StreamPipes;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass(StreamPipes.DATA_SINK_INVOCATION)
@Entity
public class DataSinkInvocation extends InvocableStreamPipesEntity {

  private static final long serialVersionUID = -2345635798917416757L;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_EC_TYPE)
  private List<String> category;

  public DataSinkInvocation(DataSinkInvocation sec) {
    super(sec);
    this.category = sec.getCategory();
  }

  public DataSinkInvocation(DataSinkDescription sec) {
    super();
    this.setName(sec.getName());
    this.setDescription(sec.getDescription());
    this.setIconUrl(sec.getIconUrl());
    this.setInputStreams(sec.getSpDataStreams());
    this.setSupportedGrounding(sec.getSupportedGrounding());
    this.setStaticProperties(sec.getStaticProperties());
    this.setBelongsTo(sec.getElementId());
    this.category = sec.getCategory();
    this.setStreamRequirements(sec.getSpDataStreams());
    this.setAppId(sec.getAppId());
    this.setIncludesAssets(sec.isIncludesAssets());
    //this.setUri(belongsTo +"/" +getElementId());
  }

  public DataSinkInvocation(DataSinkDescription sec, String domId) {
    this(sec);
    this.setDOM(domId);
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
