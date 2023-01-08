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
package org.apache.streampipes.model.template;

import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.util.Cloner;

import java.util.ArrayList;
import java.util.List;

public class BoundPipelineElement {

  private InvocableStreamPipesEntity pipelineElementTemplate;

  private List<BoundPipelineElement> connectedTo;

  public BoundPipelineElement() {
    super();
    this.connectedTo = new ArrayList<>();
  }

  public BoundPipelineElement(BoundPipelineElement other) {
    this.pipelineElementTemplate = other.getPipelineElementTemplate();
    this.connectedTo = new Cloner().boundPipelineElements(other.getConnectedTo());
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
