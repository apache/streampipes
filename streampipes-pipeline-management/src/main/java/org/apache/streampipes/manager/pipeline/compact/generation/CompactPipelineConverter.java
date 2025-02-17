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

package org.apache.streampipes.manager.pipeline.compact.generation;

import org.apache.streampipes.manager.template.CompactConfigGenerator;
import org.apache.streampipes.model.output.CustomOutputStrategy;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.output.UserDefinedOutputStrategy;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.compact.CompactPipelineElement;
import org.apache.streampipes.model.pipeline.compact.OutputConfiguration;
import org.apache.streampipes.model.pipeline.compact.UserDefinedOutput;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.staticproperty.StaticProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompactPipelineConverter {

  public List<CompactPipelineElement> convert(Pipeline pipeline) {
    var pipelineElements = new ArrayList<CompactPipelineElement>();

    pipeline.getStreams().stream()
        .map(stream -> createElement(
            PipelineElementConfigurationStep.STREAM_TYPE,
            stream.getDom(),
            stream.getElementId(),
            null,
            null,
            null))
        .forEach(pipelineElements::add);

    pipeline.getSepas().stream()
        .map(processor -> createElement(
            PipelineElementConfigurationStep.PROCESSOR_TYPE,
            processor.getDom(),
            processor.getAppId(),
            processor.getConnectedTo(),
            getConfig(processor.getStaticProperties()),
            getOutput(processor.getOutputStrategies().get(0))))
        .forEach(pipelineElements::add);

    pipeline.getActions().stream()
        .map(sink -> createElement(
            PipelineElementConfigurationStep.SINK_TYPE,
            sink.getDom(),
            sink.getAppId(),
            sink.getConnectedTo(),
            getConfig(sink.getStaticProperties()),
            null))
        .forEach(pipelineElements::add);

    return pipelineElements;
  }

  private CompactPipelineElement createElement(String type,
                                               String ref,
                                               String elementId,
                                               List<String> connectedTo,
                                               List<Map<String, Object>> config,
                                               OutputConfiguration outputConfiguration) {
    var connections = connectedTo != null ? connectedTo.stream()
        .map(this::replaceId)
        .toList() : null;
    return new CompactPipelineElement(type, replaceId(ref), elementId, connections, config, outputConfiguration);
  }

  public List<Map<String, Object>> getConfig(List<StaticProperty> staticProperties) {
    var configs = new ArrayList<Map<String, Object>>();
    staticProperties.forEach(c -> configs.add(new CompactConfigGenerator(c).toTemplateValue()));
    return configs;
  }

  public OutputConfiguration getOutput(OutputStrategy outputStrategy) {
    if (outputStrategy instanceof CustomOutputStrategy) {
      return new OutputConfiguration(((CustomOutputStrategy) outputStrategy).getSelectedPropertyKeys(), null);
    } else if (outputStrategy instanceof UserDefinedOutputStrategy) {
      return new OutputConfiguration(
          null,
          toCustomConfig(((UserDefinedOutputStrategy) outputStrategy).getEventProperties())
      );
    } else {
      return null;
    }
  }

  private List<UserDefinedOutput> toCustomConfig(List<EventProperty> eventProperties) {
    return eventProperties.stream().map(ep -> new UserDefinedOutput(
        ep.getRuntimeName(),
        ((EventPropertyPrimitive) ep).getRuntimeType(),
        ep.getSemanticType())).toList();
  }

  private String replaceId(String id) {
    return id != null ? id.replaceAll(InvocablePipelineElementGenerator.ID_PREFIX, "") : null;
  }
}
