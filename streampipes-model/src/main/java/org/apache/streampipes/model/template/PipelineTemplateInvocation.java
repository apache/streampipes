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
package org.apache.streampipes.model.template;

import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.util.Cloner;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
@TsModel
public class PipelineTemplateInvocation {

  private String kviName;

  private String dataStreamId;

  private String pipelineTemplateId;

  private PipelineTemplateDescription pipelineTemplateDescription;

  private List<StaticProperty> staticProperties;

  public PipelineTemplateInvocation() {
    super();
    this.staticProperties = new ArrayList<>();
  }

  public PipelineTemplateInvocation(PipelineTemplateInvocation other) {
    this.kviName = other.getKviName();
    this.dataStreamId = other.getDataStreamId();
    this.pipelineTemplateId = other.getPipelineTemplateId();

    if (other.getStaticProperties() != null) {
      this.staticProperties = new Cloner().staticProperties(other.getStaticProperties());
    }
  }

  public String getKviName() {
    return kviName;
  }

  public void setKviName(String kviName) {
    this.kviName = kviName;
  }

  public String getDataStreamId() {
    return dataStreamId;
  }

  public void setDataStreamId(String dataStreamId) {
    this.dataStreamId = dataStreamId;
  }

  public List<StaticProperty> getStaticProperties() {
    return staticProperties;
  }

  public void setStaticProperties(List<StaticProperty> staticProperties) {
    this.staticProperties = staticProperties;
  }

  public PipelineTemplateDescription getPipelineTemplateDescription() {
    return pipelineTemplateDescription;
  }

  public void setPipelineTemplateDescription(PipelineTemplateDescription pipelineTemplateDescription) {
    this.pipelineTemplateDescription = pipelineTemplateDescription;
  }

  public String getPipelineTemplateId() {
    return pipelineTemplateId;
  }

  public void setPipelineTemplateId(String pipelineTemplateId) {
    this.pipelineTemplateId = pipelineTemplateId;
  }
}
