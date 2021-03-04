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

package org.apache.streampipes.manager.execution.pipeline;

import org.apache.streampipes.manager.execution.http.GraphSubmitter;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.manager.execution.status.PipelineStatusManager;
import org.apache.streampipes.manager.util.TemporaryGraphStorage;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.message.PipelineStatusMessage;
import org.apache.streampipes.model.message.PipelineStatusMessageType;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

import java.util.*;
import java.util.stream.Collectors;

public class PipelineExecutor extends AbstractPipelineExecutor {

    public PipelineExecutor(Pipeline pipeline, boolean visualize, boolean storeStatus, boolean monitor) {
        super(pipeline, visualize, storeStatus, monitor);
    }

    public PipelineOperationStatus startPipeline() {

        pipeline.getSepas().forEach(this::updateKafkaGroupIds);
        pipeline.getActions().forEach(this::updateKafkaGroupIds);

        List<DataProcessorInvocation> sepas = pipeline.getSepas();
        List<DataSinkInvocation> secs = pipeline.getActions();

        List<SpDataSet> dataSets = pipeline.getStreams().stream().filter(s -> s instanceof SpDataSet).map(s -> new
                SpDataSet((SpDataSet) s)).collect(Collectors.toList());

        for (SpDataSet ds : dataSets) {
          ds.setCorrespondingPipeline(pipeline.getPipelineId());
        }

        List<InvocableStreamPipesEntity> graphs = new ArrayList<>();
        graphs.addAll(sepas);
        graphs.addAll(secs);

        List<InvocableStreamPipesEntity> decryptedGraphs = decryptSecrets(graphs);

        graphs.forEach(g -> g.setStreamRequirements(Collections.emptyList()));

        List<SpDataStreamRelayContainer> relays = generateRelays(decryptedGraphs);

        PipelineOperationStatus status = new GraphSubmitter(pipeline.getPipelineId(), pipeline.getName(),
                decryptedGraphs, dataSets, relays).invokePipelineElementsAndRelays();

        if (status.isSuccess()) {
            storeInvocationGraphs(pipeline.getPipelineId(), graphs, dataSets);
            storeDataStreamRelayContainer(relays);

            PipelineStatusManager.addPipelineStatus(
                  pipeline.getPipelineId(),
                  new PipelineStatusMessage(pipeline.getPipelineId(),
                          System.currentTimeMillis(),
                          PipelineStatusMessageType.PIPELINE_STARTED.title(),
                          PipelineStatusMessageType.PIPELINE_STARTED.description()));

          if (storeStatus) setPipelineStarted(pipeline);
        }
        return status;
    }

    public PipelineOperationStatus stopPipeline() {
        List<InvocableStreamPipesEntity> graphs = TemporaryGraphStorage.graphStorage.get(pipeline.getPipelineId());
        List<SpDataSet> dataSets = TemporaryGraphStorage.datasetStorage.get(pipeline.getPipelineId());
        List<SpDataStreamRelayContainer> relays = generateRelays(graphs);

        PipelineOperationStatus status = new GraphSubmitter(pipeline.getPipelineId(), pipeline.getName(), graphs,
                dataSets, relays).detachPipelineElementsAndRelays();

        if (status.isSuccess()) {
            if (visualize) deleteVisualization(pipeline.getPipelineId());
            if (storeStatus) setPipelineStopped(pipeline);

            deleteDataStreamRelayContainer(relays);

            PipelineStatusManager.addPipelineStatus(pipeline.getPipelineId(),
                  new PipelineStatusMessage(pipeline.getPipelineId(),
                          System.currentTimeMillis(),
                          PipelineStatusMessageType.PIPELINE_STOPPED.title(),
                          PipelineStatusMessageType.PIPELINE_STOPPED.description()));
        }
        return status;
    }

}
