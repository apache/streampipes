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

package org.apache.streampipes.manager.execution.task;

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.manager.execution.PipelineExecutionInfo;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointProvider;
import org.apache.streampipes.model.api.EndpointSelectable;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementStatus;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

import java.util.List;
import java.util.stream.Collectors;

public class DiscoverEndpointsTask implements PipelineExecutionTask {
  @Override
  public void executeTask(Pipeline pipeline,
                          PipelineExecutionInfo executionInfo) {
    var processorsAndSinks = executionInfo.getProcessorsAndSinks();

    processorsAndSinks.forEach(el -> {
      try {
        var endpointUrl = findSelectedEndpoint(el);
        applyEndpointAndPipeline(pipeline.getPipelineId(), el, endpointUrl);
      } catch (NoServiceEndpointsAvailableException e) {
        executionInfo.addFailedPipelineElement(el);
      }
    });

    var failedServices = executionInfo.getFailedServices();
    if (executionInfo.getFailedServices().size() > 0) {
      List<PipelineElementStatus> pe = failedServices
          .stream()
          .map(fs -> new PipelineElementStatus(fs.getElementId(), fs.getName(), false,
              "No active extensions service found which provides this pipeline element"))
          .collect(Collectors.toList());
      var status = new PipelineOperationStatus(pipeline.getPipelineId(),
          pipeline.getName(),
          "Could not start pipeline " + pipeline.getName() + ".",
          pe);
      executionInfo.applyPipelineOperationStatus(status);
    }
  }

  private void applyEndpointAndPipeline(String pipelineId,
                                        EndpointSelectable pipelineElement,
                                        String endpointUrl) {
    pipelineElement.setSelectedEndpointUrl(endpointUrl);
    pipelineElement.setCorrespondingPipeline(pipelineId);
  }

  private String findSelectedEndpoint(InvocableStreamPipesEntity pipelineElement)
      throws NoServiceEndpointsAvailableException {
    return new ExtensionsServiceEndpointProvider().findSelectedEndpoint(pipelineElement);
  }

}
