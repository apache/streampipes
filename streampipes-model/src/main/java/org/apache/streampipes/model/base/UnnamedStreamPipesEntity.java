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


import io.fogsy.empire.annotations.RdfId;
import io.fogsy.empire.annotations.RdfProperty;
import org.apache.streampipes.model.shared.annotation.TsIgnore;
import org.apache.streampipes.model.util.RdfIdGenerator;
import org.apache.streampipes.vocabulary.StreamPipes;

/**
 * unnamed SEPA elements (that do not require any readable identifier)
 */
public abstract class UnnamedStreamPipesEntity extends AbstractStreamPipesEntity {

  private static final long serialVersionUID = 8051137255998890188L;

  @RdfId
  @RdfProperty(StreamPipes.HAS_ELEMENT_NAME)
  @TsIgnore
  private String elementId;


  public UnnamedStreamPipesEntity() {
    super();
    this.elementId = RdfIdGenerator.makeRdfId(this);
  }

  public UnnamedStreamPipesEntity(UnnamedStreamPipesEntity other) {
    this();
  }

  public UnnamedStreamPipesEntity(String elementId) {
    super();
    this.elementId = elementId;
  }

  public String getElementId() {
    return elementId;
  }

  public void setElementId(String elementId) {
    this.elementId = elementId;
  }
}
