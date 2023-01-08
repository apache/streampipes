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
import org.apache.streampipes.extensions.management.connect.adapter.AdapterPipelineGenerator;
import org.apache.streampipes.model.connect.guess.AdapterEventPreview;
import org.apache.streampipes.model.connect.guess.GuessTypeInfo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdapterEventPreviewPipeline implements IAdapterPipeline {

  private List<IAdapterPipelineElement> pipelineElements;
  private Map<String, GuessTypeInfo> event;

  public AdapterEventPreviewPipeline(AdapterEventPreview previewRequest) {
    this.pipelineElements = new AdapterPipelineGenerator().makeAdapterPipelineElements(previewRequest.getRules());
    this.event = previewRequest.getInputData();
  }

  @Override
  public void process(Map<String, Object> event) {
    for (IAdapterPipelineElement pe : this.pipelineElements) {
      event = pe.process(event);
    }
  }

  @Override
  public List<IAdapterPipelineElement> getPipelineElements() {
    return null;
  }

  @Override
  public void setPipelineElements(List<IAdapterPipelineElement> pipelineElements) {

  }

  @Override
  public void changePipelineSink(IAdapterPipelineElement pipelineSink) {

  }

  @Override
  public IAdapterPipelineElement getPipelineSink() {
    return null;
  }

  public Map<String, GuessTypeInfo> makePreview() {
    Map<String, Object> ev = this.event
        .entrySet()
        .stream()
        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getValue()));
    this.process(ev);

    return ev
        .entrySet()
        .stream()
        .collect(Collectors.toMap(Map.Entry::getKey,
            e -> new GuessTypeInfo(e.getValue().getClass().getCanonicalName(), e.getValue())));
  }
}
