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
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@RdfsClass(StreamPipes.BOUND_PIPELINE_ELEMENT)
@Entity
public class BoundPipelineElement extends UnnamedStreamPipesEntity {

  @RdfProperty(StreamPipes.HAS_PIPELINE_ELEMENT_TEMPLATE)
  private InvocableStreamPipesEntity pipelineElementTemplate;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.IS_CONNECTED_TO)
  private List<BoundPipelineElement> connectedTo;

  public BoundPipelineElement() {
    super();
    this.connectedTo = new ArrayList<>();
  }

  public BoundPipelineElement(BoundPipelineElement other) {
    // TODO add cloner
    super(other);
    this.pipelineElementTemplate = other.getPipelineElementTemplate();
    this.connectedTo = other.getConnectedTo();
  }

  public InvocableStreamPipesEntity getPipelineElementTemplate() {
    return pipelineElementTemplate;
  }

  public void setPipelineElementTemplate(InvocableStreamPipesEntity pipelineElementTemplate) {
    this.pipelineElementTemplate = pipelineElementTemplate;
  }

  public List<BoundPipelineElement> getConnectedTo() {
    return connectedTo;
  }

  public void setConnectedTo(List<BoundPipelineElement> connectedTo) {
    this.connectedTo = connectedTo;
  }
}
