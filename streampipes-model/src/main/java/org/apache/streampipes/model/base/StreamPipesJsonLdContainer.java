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

package org.apache.streampipes.model.base;

import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

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

  public StreamPipesJsonLdContainer(StreamPipesJsonLdContainer other) {
    super(other);
    this.containedElements = other.getContainedElements();
  }

  public List<? extends AbstractStreamPipesEntity> getContainedElements() {
    return containedElements;
  }

  public void setContainedElements(List<AbstractStreamPipesEntity> containedElements) {
    this.containedElements = containedElements;
  }
}
