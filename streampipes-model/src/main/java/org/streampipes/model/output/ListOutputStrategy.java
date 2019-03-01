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

package org.streampipes.model.output;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.LIST_OUTPUT_STRATEGY)
@Entity
public class ListOutputStrategy extends OutputStrategy {

  private static final long serialVersionUID = -6400256021072543325L;

  @RdfProperty(StreamPipes.LIST_PROPERTY_NAME)
  private String propertyName;

  public ListOutputStrategy() {
    super();
  }

  public ListOutputStrategy(ListOutputStrategy other) {
    super(other);
    this.propertyName = other.getPropertyName();
  }

  public ListOutputStrategy(String propertyName) {
    super();
    this.propertyName = propertyName;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }


}
