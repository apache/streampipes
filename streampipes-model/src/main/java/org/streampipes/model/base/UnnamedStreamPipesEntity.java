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

package org.streampipes.model.base;


import org.apache.commons.lang.RandomStringUtils;
import org.streampipes.empire.annotations.RdfId;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.vocabulary.StreamPipes;

/**
 * unnamed SEPA elements (that do not require any readable identifier)
 */
public abstract class UnnamedStreamPipesEntity extends AbstractStreamPipesEntity {

  private static final long serialVersionUID = 8051137255998890188L;

  private static final String prefix = "urn:streampipes.org:spi:";

  @RdfId
  @RdfProperty(StreamPipes.HAS_ELEMENT_NAME)
  private String elementId;


  public UnnamedStreamPipesEntity() {
    super();
    this.elementId = prefix
            + this.getClass().getSimpleName().toLowerCase()
            + ":"
            + RandomStringUtils.randomAlphabetic(6);
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
