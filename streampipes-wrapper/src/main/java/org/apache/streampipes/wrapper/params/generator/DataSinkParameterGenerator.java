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

import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.extensions.api.pe.param.IParameterGenerator;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.output.PropertyRenameRule;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.wrapper.params.DataSinkParameters;

import java.util.Collections;
import java.util.List;

public class DataSinkParameterGenerator
    extends PipelineElementParameterGenerator<DataSinkInvocation>
    implements IParameterGenerator<DataSinkInvocation, IDataSinkParameterExtractor, IDataSinkParameters> {

  @Override
  public IDataSinkParameters makeParameters(DataSinkInvocation pipelineElement) {

    return new DataSinkParameters(
        pipelineElement,
        DataSinkParameterExtractor.from(pipelineElement),
        buildInputStreamParams(pipelineElement),
        buildInEventTypes(pipelineElement)
    );
  }

  @Override
  protected List<PropertyRenameRule> getRenameRules(DataSinkInvocation pipelineElementInvocation) {
    return Collections.emptyList();
  }
}
