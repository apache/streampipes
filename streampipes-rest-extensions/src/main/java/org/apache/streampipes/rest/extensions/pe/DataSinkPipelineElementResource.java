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
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.config.IDataSinkConfiguration;
import org.apache.streampipes.extensions.api.pe.runtime.IDataSinkRuntime;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.init.RunningInstances;
import org.apache.streampipes.extensions.management.util.GroundingDebugUtils;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.svcdiscovery.api.model.SpServicePathPrefix;

import jakarta.ws.rs.Path;

import java.util.Map;

@Path(SpServicePathPrefix.DATA_SINK)
public class DataSinkPipelineElementResource extends InvocablePipelineElementResource
    <DataSinkInvocation,
        IStreamPipesDataSink,
        IDataSinkConfiguration,
        IDataSinkRuntime,
        DataSinkParameterExtractor> {

  public DataSinkPipelineElementResource() {
    super(DataSinkInvocation.class);
  }

  @Override
  protected Map<String, IStreamPipesDataSink> getElementDeclarers() {
    return DeclarersSingleton.getInstance().getDataSinks();
  }

  @Override
  protected String getInstanceId(String uri, String elementId) {
    //return Util.getInstanceId(uri, PipelineElementPrefix.DATA_SINK, elementId);
    return InstanceIdExtractor.extractId(uri);
  }

  @Override
  protected DataSinkParameterExtractor getExtractor(DataSinkInvocation graph) {
    return new DataSinkParameterExtractor(graph);
  }

  @Override
  protected DataSinkInvocation createGroundingDebugInformation(DataSinkInvocation graph) {
    graph.getInputStreams().forEach(is -> {
      GroundingDebugUtils.modifyGrounding(is.getEventGrounding());
    });

    return graph;
  }

  @Override
  protected IDataSinkRuntime getRuntime() {
    return DeclarersSingleton.getInstance().getRuntimeProviders().get(0).getDataSinkRuntime().get();
  }

  @Override
  protected Response invokeRuntime(String runningInstanceId,
                                   IStreamPipesDataSink pipelineElement,
                                   DataSinkInvocation graph) {
    var runtime = getRuntime();
    var response = runtime.onRuntimeInvoked(runningInstanceId, pipelineElement, graph);
    RunningInstances.INSTANCE.add(runningInstanceId, graph, runtime);
    return response;
  }
}
