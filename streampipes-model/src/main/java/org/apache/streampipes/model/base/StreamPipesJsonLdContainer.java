/*
Copyright 2020 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.apache.streampipes.model.base;

import org.apache.streampipes.vocabulary.StreamPipes;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass(StreamPipes.ENTITY_CONTAINER)
public class StreamPipesJsonLdContainer extends UnnamedStreamPipesEntity {

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.CONTAINS_ELEMENTS)
  private List<? extends AbstractStreamPipesEntity> containedElements;

  public StreamPipesJsonLdContainer() {
    super();
  }

  public StreamPipesJsonLdContainer(List<? extends AbstractStreamPipesEntity> containedElements) {
    super();
    this.containedElements = containedElements;
  }

  public List<? extends AbstractStreamPipesEntity> getContainedElements() {
    return containedElements;
  }

  public void setContainedElements(List<AbstractStreamPipesEntity> containedElements) {
    this.containedElements = containedElements;
  }
}
