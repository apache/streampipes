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
package org.streampipes.model.template;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@RdfsClass(StreamPipes.PIPELINE_TEMPLATE_DESCRIPTION)
@Entity
public class PipelineTemplateDescription extends NamedStreamPipesEntity {

  //@RdfProperty(RDFS.LABEL)
  //private String pipelineTemplateName;

  //@RdfProperty(StreamPipes.INTERNAL_NAME)
  //private String pipelineTemplateId;

  //@RdfProperty(RDFS.DESCRIPTION)
  //private String pipelineTemplateDescription;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.IS_CONNECTED_TO)
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

  public PipelineTemplateDescription(String elementName, SpDataStream requiredStream, List<BoundPipelineElement> connectedTo) {
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
