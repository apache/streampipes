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
package org.apache.streampipes.manager.execution.pipeline.executor.operations;

import org.apache.streampipes.manager.execution.http.GraphSubmitter;
import org.apache.streampipes.manager.execution.pipeline.executor.PipelineExecutor;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.PipelineElementUtils;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.StatusUtils;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class StartPipelineOperation extends PipelineExecutionOperation{

    public StartPipelineOperation(PipelineExecutor pipelineExecutor) {
        super(pipelineExecutor);
    }

    @Override
    public PipelineOperationStatus executeOperation() {
        Pipeline pipeline = associatedPipelineExecutor.getPipeline();
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

        List<InvocableStreamPipesEntity> decryptedGraphs = PipelineElementUtils.decryptSecrets(graphs, pipeline);

        graphs.forEach(g -> g.setStreamRequirements(Collections.emptyList()));

        List<SpDataStreamRelayContainer> relays = PipelineElementUtils.generateRelays(decryptedGraphs, pipeline);

        associatedPipelineExecutor.setGraphs(graphs);
        associatedPipelineExecutor.setDataSets(dataSets);
        associatedPipelineExecutor.setRelays(relays);

        return new GraphSubmitter(pipeline.getPipelineId(), pipeline.getName(),
                decryptedGraphs, dataSets, relays).invokePipelineElementsAndRelays();
    }

    @Override
    public PipelineOperationStatus rollbackOperationPartially() {
        //TODO: Check if there is a possible realization of partial rollback
        return StatusUtils.initPipelineOperationStatus(associatedPipelineExecutor.getPipeline());
    }

    @Override
    public PipelineOperationStatus rollbackOperationFully() {
        //TODO: Implement full rollback
        return StatusUtils.initPipelineOperationStatus(associatedPipelineExecutor.getPipeline());
    }

    /**
     * Updates group.id for data processor/sink. Note: KafkaTransportProtocol only!!
     *
     * @param entity    data processor/sink
     */
    private void updateKafkaGroupIds(InvocableStreamPipesEntity entity) {
        entity.getInputStreams()
                .stream()
                .filter(is -> is.getEventGrounding().getTransportProtocol() instanceof KafkaTransportProtocol)
                .map(is -> is.getEventGrounding().getTransportProtocol())
                .map(KafkaTransportProtocol.class::cast)
                .forEach(tp -> tp.setGroupId(UUID.randomUUID().toString()));
    }
}
