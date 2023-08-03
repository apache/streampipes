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


package org.apache.streampipes.connect.management;

import org.apache.streampipes.connect.shared.AdapterPipelineGeneratorBase;
import org.apache.streampipes.extensions.api.connect.IAdapterPipeline;
import org.apache.streampipes.extensions.api.connect.IAdapterPipelineElement;
import org.apache.streampipes.model.connect.guess.AdapterEventPreview;
import org.apache.streampipes.model.schema.EventSchema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterEventPreviewPipeline implements IAdapterPipeline {

  private final List<IAdapterPipelineElement> pipelineElements;
  private final String event;

  private ObjectMapper objectMapper;

  public AdapterEventPreviewPipeline(AdapterEventPreview previewRequest) {
    this.objectMapper = new ObjectMapper();
    this.pipelineElements = new AdapterPipelineGeneratorBase().makeAdapterPipelineElements(previewRequest.getRules());
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

  public String makePreview() throws JsonProcessingException {
    var ev = objectMapper.readValue(event, HashMap.class);
    this.process(ev);

    return this.objectMapper.writeValueAsString(ev);
  }

  @Override
  public EventSchema getResultingEventSchema() {
    return null;
  }
}
