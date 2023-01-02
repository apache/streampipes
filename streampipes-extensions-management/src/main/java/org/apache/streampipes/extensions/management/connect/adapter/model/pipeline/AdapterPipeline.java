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

import org.apache.streampipes.extensions.api.connect.IAdapterPipeline;
import org.apache.streampipes.extensions.api.connect.IAdapterPipelineElement;

import java.util.List;
import java.util.Map;

public class AdapterPipeline implements IAdapterPipeline {

  private List<IAdapterPipelineElement> pipelineElements;
  private IAdapterPipelineElement pipelineSink;


  public AdapterPipeline(List<IAdapterPipelineElement> pipelineElements) {
    this.pipelineElements = pipelineElements;
  }

  public AdapterPipeline(List<IAdapterPipelineElement> pipelineElements, IAdapterPipelineElement pipelineSink) {
    this.pipelineElements = pipelineElements;
    this.pipelineSink = pipelineSink;
  }

  @Override
  public void process(Map<String, Object> event) {

    // TODO remove, just for performance tests
    if ("true".equals(System.getenv("SP_DEBUG_CONNECT"))) {
      event.put("internal_t1", System.currentTimeMillis());
    }


    for (IAdapterPipelineElement pipelineElement : pipelineElements) {
      event = pipelineElement.process(event);
    }
    if (pipelineSink != null) {
      pipelineSink.process(event);
    }

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
}
