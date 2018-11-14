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
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@RdfsClass(StreamPipes.PIPELINE_ELEMENT_CONTAINER)
@Entity
public class PipelineElementContainer extends NamedStreamPipesEntity {

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_PIPELINE_ELEMENTS)
  private List<NamedStreamPipesEntity> pipelineElementDescriptions;

  public PipelineElementContainer(PipelineElementContainer other) {
    super(other);
    if (other.getPipelineElementDescriptions() != null) {
      this.pipelineElementDescriptions = new Cloner().cloneDescriptions(other.getPipelineElementDescriptions());
    }
  }

  public PipelineElementContainer(String containerId, String name, String description)
  {
    super(containerId, name, description);
    this.pipelineElementDescriptions = new ArrayList<>();
  }

  public List<NamedStreamPipesEntity> getPipelineElementDescriptions() {
    return pipelineElementDescriptions;
  }

  public void setPipelineElementDescriptions(List<NamedStreamPipesEntity> pipelineElementDescriptions) {
    this.pipelineElementDescriptions = pipelineElementDescriptions;
  }
}
