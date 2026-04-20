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

package org.apache.streampipes.extensions.management.connect.adapter.model.pipeline;

import org.apache.streampipes.connect.shared.preprocessing.elements.ScriptTransformationPipelineElement;
import org.apache.streampipes.connect.transformer.api.Context;
import org.apache.streampipes.extensions.api.connect.IAdapterPipeline;
import org.apache.streampipes.extensions.api.connect.IAdapterPipelineElement;
import org.apache.streampipes.model.connect.TransformationConfig;
import org.apache.streampipes.model.schema.EventSchema;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AdapterPipeline implements IAdapterPipeline {

  private List<IAdapterPipelineElement> pipelineElements;
  private IAdapterPipelineElement pipelineSink;

  private final EventSchema resultingEventSchema;
  private final Function<Map<String, Object>, List<Map<String, Object>>> processingFn;

  public AdapterPipeline(List<IAdapterPipelineElement> pipelineElements,
                         TransformationConfig transformationConfig,
                         Context scriptContext,
                         EventSchema resultingEventSchema) {
    this.pipelineElements = pipelineElements;
    this.resultingEventSchema = resultingEventSchema;
    if (transformationConfig.isScriptActive()) {
      var transformation = new ScriptTransformationPipelineElement(
          transformationConfig.getLanguage(),
          transformationConfig.getScript(),
          scriptContext
      );
      processingFn = transformation::process;
    } else {
      processingFn = List::of;
    }
  }

  public AdapterPipeline(List<IAdapterPipelineElement> pipelineElements,
                         TransformationConfig transformationConfig,
                         IAdapterPipelineElement pipelineSink,
                         Context scriptContext,
                         EventSchema resultingEventSchema) {
    this(pipelineElements, transformationConfig, scriptContext, resultingEventSchema);
    this.pipelineSink = pipelineSink;
  }

  @Override
  public void process(Map<String, Object> event) {
    var scriptResult = this.processingFn.apply(event);
    scriptResult.forEach(result -> {
      for (IAdapterPipelineElement pipelineElement : pipelineElements) {
        result = pipelineElement.process(result);
      }
      if (pipelineSink != null) {
        pipelineSink.process(result);
      }
    });
  }

  @Override
  public List<IAdapterPipelineElement> getPipelineElements() {
    return pipelineElements;
  }

  @Override
  public void setPipelineElements(List<IAdapterPipelineElement> pipelineElements) {
    this.pipelineElements = pipelineElements;
  }

  @Override
  public void changePipelineSink(IAdapterPipelineElement pipelineSink) {
    this.pipelineSink = pipelineSink;
  }

  @Override
  public IAdapterPipelineElement getPipelineSink() {
    return pipelineSink;
  }

  @Override
  public EventSchema getResultingEventSchema() {
    return resultingEventSchema;
  }
}
