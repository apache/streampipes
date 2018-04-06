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
package org.streampipes.manager.template;

import org.streampipes.manager.operations.Operations;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.model.client.pipeline.PipelineOperationStatus;
import org.streampipes.model.template.PipelineTemplateDescription;
import org.streampipes.model.template.PipelineTemplateInvocation;

public class PipelineTemplateInvocationHandler {

  private PipelineTemplateInvocation pipelineTemplateInvocation;

  public PipelineTemplateInvocationHandler(PipelineTemplateInvocation pipelineTemplateInvocation) {
    this.pipelineTemplateInvocation = pipelineTemplateInvocation;
  }


  public PipelineOperationStatus handlePipelineInvocation() {
    Pipeline pipeline = new PipelineGenerator(pipelineTemplateInvocation.getDataSetId(), getTemplateById(pipelineTemplateInvocation.getPipelineTemplateId())).makePipeline();

    Operations.storePipeline(pipeline);
    return Operations.startPipeline(pipeline);

  }

  private PipelineTemplateDescription getTemplateById(String pipelineTemplateId) {
    return new PipelineTemplateGenerator().makeExampleTemplates().stream().filter(template -> template.getPipelineTemplateId().equals(pipelineTemplateId)).findFirst().get();
  }
}
