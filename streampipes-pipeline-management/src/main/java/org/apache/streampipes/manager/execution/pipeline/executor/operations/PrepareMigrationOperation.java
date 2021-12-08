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

import org.apache.streampipes.manager.data.PipelineGraph;
import org.apache.streampipes.manager.data.PipelineGraphBuilder;
import org.apache.streampipes.manager.data.PipelineGraphHelpers;
import org.apache.streampipes.manager.execution.pipeline.executor.PipelineExecutor;
import org.apache.streampipes.manager.execution.pipeline.executor.operations.types.MigrationOperation;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.PipelineUtils;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.StatusUtils;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.model.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PrepareMigrationOperation extends PipelineExecutionOperation implements MigrationOperation {

    public PrepareMigrationOperation(PipelineExecutor pipelineExecutor) {
        super(pipelineExecutor);
    }

    @Override
    public PipelineOperationStatus executeOperation() {
        Pipeline pipeline = associatedPipelineExecutor.getPipeline();
        //Purge existing relays
        PipelineUtils.purgeExistingRelays(pipeline);

        //Generate new relays
        PipelineGraph pipelineGraphAfterMigration =
                new PipelineGraphBuilder(pipeline).buildGraph();
        PipelineUtils.buildPipelineGraph(pipelineGraphAfterMigration, pipeline.getPipelineId());

        //Get predecessors
        PipelineGraph pipelineGraphBeforeMigration =
                new PipelineGraphBuilder(associatedPipelineExecutor.getSecondaryPipeline()).buildGraph();
        findPredecessorsInMigrationPipeline(pipelineGraphAfterMigration);

        //Find counterpart for predecessors in currentPipeline
        findAndComparePredecessorsInCurrentPipeline(pipelineGraphBeforeMigration);
        return StatusUtils.initPipelineOperationStatus(pipeline);
    }

    @Override
    public PipelineOperationStatus rollbackOperationPartially() {
        return null;
    }

    @Override
    public PipelineOperationStatus rollbackOperationFully() {
        return null;
    }

    private void findPredecessorsInMigrationPipeline(PipelineGraph pipelineGraphAfterMigration) {
        // get unique list of predecessors
        List<NamedStreamPipesEntity> predecessors = PipelineGraphHelpers.findStreams(pipelineGraphAfterMigration).stream()
                .map(stream -> PipelineUtils.getPredecessors(stream,
                        associatedPipelineExecutor.getMigrationEntity().getTargetElement(),
                        pipelineGraphAfterMigration, new ArrayList<>()))
                .flatMap(List::stream)
                .collect(Collectors.toList())
                .stream()
                .distinct()
                .collect(Collectors.toList());

        associatedPipelineExecutor.getPredecessorsAfterMigration().addAll(predecessors);
    }

    private void findAndComparePredecessorsInCurrentPipeline(PipelineGraph pipelineGraphBeforeMigration) {
        associatedPipelineExecutor.getPredecessorsAfterMigration().forEach(migrationPredecessor ->
                associatedPipelineExecutor.getPredecessorsBeforeMigration()
                        .add(PipelineUtils.findMatching(migrationPredecessor, pipelineGraphBeforeMigration)));
    }
}
