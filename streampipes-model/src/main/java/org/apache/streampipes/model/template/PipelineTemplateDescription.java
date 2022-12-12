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

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.model.util.Cloner;

import java.util.ArrayList;
import java.util.List;

@TsModel
public class PipelineTemplateDescription extends NamedStreamPipesEntity {

  private List<BoundPipelineElement> boundTo;

  public PipelineTemplateDescription() {
    super();
    this.boundTo = new ArrayList<>();
  }

  public PipelineTemplateDescription(String uri, String name, String description) {
    super(uri, name, description);
    this.boundTo = new ArrayList<>();
  }

  public PipelineTemplateDescription(SpDataStream requiredStream, List<BoundPipelineElement> connectedTo) {
    super();
    this.boundTo = connectedTo == null ? new ArrayList<>() : connectedTo;
  }

  public PipelineTemplateDescription(PipelineTemplateDescription other) {
    super(other);
    // TODO use cloner
    if (other.getBoundTo() != null) {
      this.boundTo = new Cloner().boundPipelineElements(other.getBoundTo());
    }
    //this.pipelineTemplateName = other.getPipelineTemplateName();
    //this.pipelineTemplateDescription = other.getPipelineTemplateDescription();
    //this.pipelineTemplateId = other.getPipelineTemplateId();
  }

  public PipelineTemplateDescription(String elementName, SpDataStream requiredStream,
                                     List<BoundPipelineElement> connectedTo) {
    super(elementName);
    this.boundTo = connectedTo;
  }

  public List<BoundPipelineElement> getBoundTo() {
    return boundTo;
  }

  public void setBoundTo(List<BoundPipelineElement> boundTo) {
    this.boundTo = boundTo;
  }

  public String getPipelineTemplateName() {
    return super.getName();
  }

  public void setPipelineTemplateName(String pipelineTemplateName) {
    super.setName(pipelineTemplateName);
  }

  public String getPipelineTemplateDescription() {
    return super.getDescription();
  }

  public void setPipelineTemplateDescription(String pipelineTemplateDescription) {
    super.setDescription(pipelineTemplateDescription);
  }

  public String getPipelineTemplateId() {
    return super.getElementId();
  }

  public void setPipelineTemplateId(String pipelineTemplateId) {
    super.setElementId(pipelineTemplateId);
    super.setAppId(pipelineTemplateId);
  }
}
