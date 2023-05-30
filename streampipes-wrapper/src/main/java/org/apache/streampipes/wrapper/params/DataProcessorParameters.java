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

package org.apache.streampipes.wrapper.params;

import org.apache.streampipes.extensions.api.extractor.IDataProcessorParameterExtractor;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.param.InputStreamParams;
import org.apache.streampipes.extensions.api.pe.param.OutputStreamParams;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.output.PropertyRenameRule;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.model.util.SchemaUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataProcessorParameters
    extends PipelineElementParameters<DataProcessorInvocation, IDataProcessorParameterExtractor>
    implements IDataProcessorParameters {

  private final Map<String, Object> outEventType;
  private final SpDataStream outputStream;
  private final String outName;
  private final OutputStreamParams outputStreamParams;


  public DataProcessorParameters(DataProcessorInvocation pipelineElementInvocation,
                                 IDataProcessorParameterExtractor parameterExtractor,
                                 List<InputStreamParams> params,
                                 Map<String, Map<String, Object>> inEventTypes) {
    super(
        pipelineElementInvocation,
        parameterExtractor,
        params,
        inEventTypes);
    this.outEventType = SchemaUtils
        .toRuntimeMap(pipelineElementInvocation.getOutputStream().getEventSchema().getEventProperties());
    this.outputStreamParams = new OutputStreamParams(pipelineElementInvocation.getOutputStream(), getRenameRules());
    outputStream = pipelineElementInvocation.getOutputStream();
    EventGrounding outputGrounding = outputStream.getEventGrounding();
    outName = outputGrounding.getTransportProtocol().getTopicDefinition().getActualTopicName();
  }

  @Override
  public Map<String, Object> getOutEventType() {
    return outEventType;
  }

  @Override
  public SchemaInfo getOutputSchemaInfo() {
    return outputStreamParams.getSchemaInfo();
  }

  @Override
  public SourceInfo getOutputSourceInfo() {
    return outputStreamParams.getSourceInfo();
  }

  public List<String> getOutputProperties() {
    return SchemaUtils.toPropertyList(outputStream.getEventSchema().getEventProperties());
  }

  @Override
  public List<PropertyRenameRule> getRenameRules() {
    return pipelineElementInvocation
        .getOutputStrategies()
        .stream()
        .flatMap(o -> o.getRenameRules().stream())
        .collect(Collectors.toList());
  }

  @Override
  public String getOutName() {
    return outName;
  }


}
