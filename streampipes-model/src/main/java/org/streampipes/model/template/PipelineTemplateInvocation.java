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
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@RdfsClass(StreamPipes.PIPELINE_TEMPLATE_INVOCATION)
@Entity
public class PipelineTemplateInvocation extends UnnamedStreamPipesEntity {

  @RdfProperty(StreamPipes.HAS_NAME)
  private String kviName;

  @RdfProperty(StreamPipes.HAS_DATASET_ID)
  private String dataSetId;

  @RdfProperty(StreamPipes.INTERNAL_NAME)
  private String pipelineTemplateId;

  //@RdfProperty(StreamPipes.HAS_PIPELINE_TEMPLATE_DESCRIPTION)
  //@OneToOne(fetch = FetchType.EAGER,
  //        cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private PipelineTemplateDescription pipelineTemplateDescription;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_STATIC_PROPERTY)
  private List<StaticProperty> staticProperties;

  public PipelineTemplateInvocation() {
    super();
    this.staticProperties = new ArrayList<>();
  }

  public String getKviName() {
    return kviName;
  }

  public void setKviName(String kviName) {
    this.kviName = kviName;
  }

  public String getDataSetId() {
    return dataSetId;
  }

  public void setDataSetId(String dataSetId) {
    this.dataSetId = dataSetId;
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
