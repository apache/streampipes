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

package org.apache.streampipes.manager.execution.http;

import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphSubmitter extends ElementSubmitter {

  public GraphSubmitter(String pipelineId, String pipelineName) {
    super(pipelineId, pipelineName, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
  }

  public GraphSubmitter(String pipelineId, String pipelineName, List<InvocableStreamPipesEntity> graphs) {
    super(pipelineId, pipelineName, graphs, new ArrayList<>(), new ArrayList<>());
  }

  public GraphSubmitter(String pipelineId, String pipelineName, List<InvocableStreamPipesEntity> graphs,
                        List<SpDataSet> dataSets, List<SpDataStreamRelayContainer> relays) {
    super(pipelineId, pipelineName, graphs, dataSets, relays);
  }

  /**
   * Called when pipeline is started. This invokes all pipeline elements including relays between adjacent pipeline
   * element nodes in the pipeline graph. Thereby, we do this along the following procedure:
   *
   * - start relays (if any)
   * - start pipeline elements
   * - start data sets (only if pipeline elements and relays started)
   *
   * We verify the status of this procedure and trigger a graceful rollback in case of any failure. In any case, we
   * return an appropriate success/error message.
   *
   * @return PipelineOperationStatus
   */
  @Override
  public PipelineOperationStatus invokePipelineElementsAndRelays() {
    PipelineOperationStatus status = initPipelineOperationStatus();

    if (relaysExist()) {
      invokeRelays(status);
    }

    invokePipelineElements(status);

    if (allInvocableEntitiesRunning(status)) {
      invokeDataSets(status);
    }

    return verifyPipelineOperationStatus(
            status,
            "Successfully started pipeline " + pipelineName,
            "Could not start pipeline " + pipelineName,
            true);
  }

  /**
   * Called when pipeline is stopped. This detaches all pipeline elements including relays between adjacent pipeline
   * element nodes in the pipeline graph. Thereby, we do this along the following procedure:
   *
   * - stop pipeline elements
   * - stop data sets
   * - stop relays (if any)
   *
   * We verify the status of this procedure and return the success/error message.
   *
   * @return PipelineOperationStatus
   */
  @Override
  public PipelineOperationStatus detachPipelineElementsAndRelays() {
    PipelineOperationStatus status = initPipelineOperationStatus();

    detachPipelineElements(status);
    detachDataSets(status);

    if (relaysExist()) {
      detachRelays(status);
    }

    return verifyPipelineOperationStatus(
            status,
            "Successfully stopped pipeline " + pipelineName,
            "Could not stop all pipeline elements of pipeline " + pipelineName,
            false);
  }

  /**
   * Called when pipeline elements are migrated and new relays need to be invoked between adjacent pipeline elements
   *
   * @return PipelineOperationStatus
   */
  @Override
  public PipelineOperationStatus invokeRelaysOnMigration() {
    PipelineOperationStatus status = initPipelineOperationStatus();

    invokeRelays(status);

    return verifyPipelineOperationStatus(
            status,
            "Successfully started relays in pipeline " + pipelineName,
            "Could not start relays in pipeline" + pipelineName,
            true);
  }

  /**
   * Called when pipeline elements are migrated and new relays need to be detached, e.g. from predecessors to old
   * pipeline element
   *
   * @return PipelineOperationStatus
   */
  @Override
  public PipelineOperationStatus detachRelaysOnMigration() {
    PipelineOperationStatus status = initPipelineOperationStatus();

    detachRelays(status);

    return verifyPipelineOperationStatus(
            status,
            "Successfully stopped relays in pipeline " + pipelineName,
            "Could not stop all relays in pipeline " + pipelineName,
            false);
  }

}
