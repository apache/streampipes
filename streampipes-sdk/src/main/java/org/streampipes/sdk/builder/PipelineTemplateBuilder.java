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
package org.streampipes.sdk.builder;

import org.streampipes.model.template.BoundPipelineElement;
import org.streampipes.model.template.PipelineTemplateDescription;

import java.util.ArrayList;
import java.util.List;

public class PipelineTemplateBuilder {

  private PipelineTemplateDescription pipelineTemplateDescription;
  private List<BoundPipelineElement> boundPipelineElements;
  private String appId;

  private PipelineTemplateBuilder(String internalId, String pipelineTemplateName, String pipelineTemplateDescription) {
    this.pipelineTemplateDescription = new PipelineTemplateDescription();
    this.pipelineTemplateDescription.setPipelineTemplateName(pipelineTemplateName);
    this.pipelineTemplateDescription.setPipelineTemplateId(internalId);
    this.pipelineTemplateDescription.setPipelineTemplateDescription(pipelineTemplateDescription);
    this.boundPipelineElements = new ArrayList<>();
  }

  public static PipelineTemplateBuilder create(String internalId, String pipelineTemplateName, String pipelineTemplateDescription) {
    return new PipelineTemplateBuilder(internalId, pipelineTemplateName, pipelineTemplateDescription);
  }

  public PipelineTemplateBuilder setAppId(String id) {
    this.pipelineTemplateDescription.setAppId(id);
    return this;
  }


  public PipelineTemplateBuilder boundPipelineElementTemplate(BoundPipelineElement boundPipelineElement) {
    this.boundPipelineElements.add(boundPipelineElement);
    return this;
  }

  public PipelineTemplateDescription build() {
    this.pipelineTemplateDescription.setBoundTo(boundPipelineElements);
    return pipelineTemplateDescription;
  }

}
