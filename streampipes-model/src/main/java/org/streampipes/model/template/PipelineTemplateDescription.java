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
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.RDFS;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@RdfsClass(StreamPipes.PIPELINE_TEMPLATE_DESCRIPTION)
@Entity
public class PipelineTemplateDescription extends UnnamedStreamPipesEntity {

  @RdfProperty(RDFS.LABEL)
  private String pipelineTemplateName;

  @RdfProperty(RDFS.DESCRIPTION)
  private String pipelineTemplateDescription;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.IS_CONNECTED_TO)
  private List<BoundPipelineElement> connectedTo;

  public PipelineTemplateDescription() {
    super();
    this.connectedTo = new ArrayList<>();
  }

  public PipelineTemplateDescription(SpDataStream requiredStream, List<BoundPipelineElement> connectedTo) {
    super();
    this.connectedTo = connectedTo;
  }

  public PipelineTemplateDescription(PipelineTemplateDescription other) {
    super(other);
    // TODO use cloner
    this.connectedTo = other.getConnectedTo();
    this.pipelineTemplateName = other.getPipelineTemplateName();
    this.pipelineTemplateDescription = other.getPipelineTemplateDescription();
  }

  public PipelineTemplateDescription(String elementName, SpDataStream requiredStream, List<BoundPipelineElement> connectedTo) {
    super(elementName);
    this.connectedTo = connectedTo;
  }

  public List<BoundPipelineElement> getConnectedTo() {
    return connectedTo;
  }

  public void setConnectedTo(List<BoundPipelineElement> connectedTo) {
    this.connectedTo = connectedTo;
  }

  public String getPipelineTemplateName() {
    return pipelineTemplateName;
  }

  public void setPipelineTemplateName(String pipelineTemplateName) {
    this.pipelineTemplateName = pipelineTemplateName;
  }

  public String getPipelineTemplateDescription() {
    return pipelineTemplateDescription;
  }

  public void setPipelineTemplateDescription(String pipelineTemplateDescription) {
    this.pipelineTemplateDescription = pipelineTemplateDescription;
  }
}
