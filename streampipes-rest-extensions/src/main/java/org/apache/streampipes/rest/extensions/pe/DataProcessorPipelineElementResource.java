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

package org.apache.streampipes.rest.extensions.pe;

import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.config.IDataProcessorConfiguration;
import org.apache.streampipes.extensions.api.pe.runtime.IDataProcessorRuntime;
import org.apache.streampipes.extensions.management.pe.DataProcessorPipelineElementManagement;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.svcdiscovery.api.model.SpServicePathPrefix;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(SpServicePathPrefix.DATA_PROCESSOR)
public class DataProcessorPipelineElementResource extends InvocablePipelineElementResource<
    DataProcessorInvocation,
    IStreamPipesDataProcessor,
    IDataProcessorConfiguration,
    IDataProcessorRuntime,
    ProcessingElementParameterExtractor> {

  public DataProcessorPipelineElementResource() {
    this(new DataProcessorPipelineElementManagement());
  }

  public DataProcessorPipelineElementResource(DataProcessorPipelineElementManagement pipelineElementManagement) {
    super(pipelineElementManagement);
  }
}
