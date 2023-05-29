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

package org.apache.streampipes.wrapper.params.generator;

import org.apache.streampipes.extensions.api.extractor.IDataProcessorParameterExtractor;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.param.IParameterGenerator;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.output.PropertyRenameRule;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.wrapper.params.DataProcessorParameters;

import java.util.List;
import java.util.stream.Collectors;

public class DataProcessorParameterGenerator
    extends PipelineElementParameterGenerator<DataProcessorInvocation>
    implements IParameterGenerator<DataProcessorInvocation,
    IDataProcessorParameterExtractor, IDataProcessorParameters> {

  @Override
  public IDataProcessorParameters makeParameters(DataProcessorInvocation pipelineElementInvocation) {
    return new DataProcessorParameters(
        pipelineElementInvocation,
        ProcessingElementParameterExtractor.from(pipelineElementInvocation),
        buildInputStreamParams(pipelineElementInvocation),
        buildInEventTypes(pipelineElementInvocation)
    );
  }


  protected List<PropertyRenameRule> getRenameRules(DataProcessorInvocation pipelineElementInvocation) {
    return pipelineElementInvocation
        .getOutputStrategies()
        .stream()
        .flatMap(o -> o.getRenameRules().stream())
        .collect(Collectors.toList());
  }
}
