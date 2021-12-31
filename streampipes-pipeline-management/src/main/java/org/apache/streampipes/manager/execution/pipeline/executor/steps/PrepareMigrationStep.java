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
package org.apache.streampipes.manager.execution.pipeline.executor.steps;

import org.apache.streampipes.manager.data.PipelineGraph;
import org.apache.streampipes.manager.data.PipelineGraphBuilder;
import org.apache.streampipes.manager.data.PipelineGraphHelpers;
import org.apache.streampipes.manager.execution.pipeline.executor.PipelineExecutor;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.PipelineElementUtils;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.PipelineUtils;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.RelayUtils;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.StatusUtils;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.model.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PrepareMigrationStep extends PipelineExecutionStep {

    private List<NamedStreamPipesEntity> predecessorsAfterMigration;
    private final List<NamedStreamPipesEntity> predecessorsBeforeMigration = new ArrayList<>();
    private Pipeline pipeline;

    public PrepareMigrationStep(PipelineExecutor pipelineExecutor) {
        super(pipelineExecutor);
    }

    @Override
    public PipelineOperationStatus executeOperation() {
        this.pipeline = pipelineExecutor.getPipeline();
        //Purge existing relays
        PipelineUtils.purgeExistingRelays(pipeline);

        //Generate new relays
        PipelineGraph pipelineGraphAfterMigration =
                new PipelineGraphBuilder(pipeline).buildGraph();
        PipelineUtils.buildPipelineGraph(pipelineGraphAfterMigration, pipeline.getPipelineId());

        //Get predecessors
        PipelineGraph pipelineGraphBeforeMigration =
                new PipelineGraphBuilder(pipelineExecutor.getSecondaryPipeline()).buildGraph();
        findPredecessorsAfterMigration(pipelineGraphAfterMigration);

        //Find counterpart for predecessors in currentPipeline
        findPredecessorsBeforeMigration(pipelineGraphBeforeMigration);

        initializeGraphs();
        initializeRelays();

        return StatusUtils.initPipelineOperationStatus(pipeline);
    }

    @Override
    public PipelineOperationStatus rollbackOperationPartially() {
        return StatusUtils.initPipelineOperationStatus(pipelineExecutor.getPipeline());
    }

    @Override
    public PipelineOperationStatus rollbackOperationFully() {
        return StatusUtils.initPipelineOperationStatus(pipelineExecutor.getPipeline());
    }

    private void findPredecessorsAfterMigration(PipelineGraph pipelineGraphAfterMigration) {
        // get unique list of predecessors
        this.predecessorsAfterMigration = PipelineGraphHelpers.findStreams(pipelineGraphAfterMigration).stream()
                .map(stream -> PipelineUtils.getPredecessors(stream,
                        pipelineExecutor.getMigrationEntity().getTargetElement(),
                        pipelineGraphAfterMigration, new ArrayList<>()))
                .flatMap(List::stream)
                .collect(Collectors.toList())
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private void findPredecessorsBeforeMigration(PipelineGraph pipelineGraphBeforeMigration) {
        this.predecessorsAfterMigration.forEach(migrationPredecessor ->
        {
            NamedStreamPipesEntity predecessor = PipelineUtils.findMatching(migrationPredecessor, pipelineGraphBeforeMigration);
            this.predecessorsBeforeMigration
                    .add(predecessor);
        });
    }

    private void initializeGraphs(){
        List<InvocableStreamPipesEntity> decryptedTargetGraphs = PipelineElementUtils.decryptSecrets(
                Collections.singletonList(pipelineExecutor.getMigrationEntity().getTargetElement()),
                pipelineExecutor.getPipeline());
        pipelineExecutor.getGraphs().getEntitiesToStart().addAll(decryptedTargetGraphs);

        List<InvocableStreamPipesEntity> originGraphs = Collections.singletonList(
                pipelineExecutor.getMigrationEntity().getSourceElement());
        pipelineExecutor.getGraphs().getEntitiesToStop().addAll(originGraphs);

        pipelineExecutor.getGraphs().getEntitiesToStore().addAll(pipeline.getActions());
        pipelineExecutor.getGraphs().getEntitiesToStore().addAll(pipeline.getSepas());
    }

    private void initializeRelays(){
        List<SpDataStreamRelayContainer> relaysToTarget = RelayUtils.findRelays(
                predecessorsAfterMigration,
                pipelineExecutor.getMigrationEntity().getTargetElement(),
                pipelineExecutor.getPipeline());
        pipelineExecutor.getRelays().getEntitiesToStart().addAll(relaysToTarget);
        pipelineExecutor.getRelays().getEntitiesToStore().addAll(relaysToTarget);

        List<SpDataStreamRelayContainer> relaysToOrigin = RelayUtils
                .findRelaysWhenStopping(
                        predecessorsBeforeMigration,
                        pipelineExecutor.getMigrationEntity().getSourceElement(),
                        pipelineExecutor.getPipeline());
        pipelineExecutor.getRelays().getEntitiesToStop().addAll(relaysToOrigin);
        pipelineExecutor.getRelays().getEntitiesToDelete().addAll(relaysToOrigin);
    }
}
