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

import org.apache.streampipes.commons.constants.InstanceIdExtractor;
import org.apache.streampipes.extensions.api.declarer.SemanticEventProcessingAgentDeclarer;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.util.GroundingDebugUtils;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.svcdiscovery.api.model.SpServicePathPrefix;

import jakarta.ws.rs.Path;

import java.util.Map;

@Path(SpServicePathPrefix.DATA_PROCESSOR)
public class DataProcessorPipelineElementResource extends InvocablePipelineElementResource<DataProcessorInvocation,
    SemanticEventProcessingAgentDeclarer, ProcessingElementParameterExtractor> {

  public DataProcessorPipelineElementResource() {

    super(DataProcessorInvocation.class);
  }

  @Override
  protected Map<String, SemanticEventProcessingAgentDeclarer> getElementDeclarers() {
    return DeclarersSingleton.getInstance().getEpaDeclarers();
  }

  @Override
  protected String getInstanceId(String uri, String elementId) {
    return InstanceIdExtractor.extractId(uri);
  }

  @Override
  protected ProcessingElementParameterExtractor getExtractor(DataProcessorInvocation graph) {
    return new ProcessingElementParameterExtractor(graph);
  }

  @Override
  protected DataProcessorInvocation createGroundingDebugInformation(DataProcessorInvocation graph) {
    graph.getInputStreams().forEach(is -> {
      GroundingDebugUtils.modifyGrounding(is.getEventGrounding());
    });

    GroundingDebugUtils.modifyGrounding(graph.getOutputStream().getEventGrounding());
    return graph;
  }
}
