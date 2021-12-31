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
package org.apache.streampipes.manager.execution.pipeline.executor;

import org.apache.streampipes.manager.execution.pipeline.executor.steps.*;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementMigrationEntity;
import org.apache.streampipes.model.pipeline.PipelineElementReconfigurationEntity;

public class PipelineExecutorBuilder {

    private PipelineExecutor pipelineExecutor;

    public static PipelineExecutorBuilder getBuilder(){
        return new PipelineExecutorBuilder();
    }

    private PipelineExecutorBuilder(){}

    public PipelineExecutorBuilder initializePipelineExecutor(Pipeline pipeline, boolean visualize, boolean storeStatus, boolean monitor){
        this.pipelineExecutor = new PipelineExecutor(pipeline, visualize, storeStatus, monitor);
        return this;
    }

    public PipelineExecutorBuilder setMigrationParameters(Pipeline pipelineBeforeMigration,
                                                          PipelineElementMigrationEntity migrationEntity){
        pipelineExecutor.setSecondaryPipeline(pipelineBeforeMigration);
        pipelineExecutor.setMigrationEntity(migrationEntity);
        return this;
    }

    public PipelineExecutorBuilder setReconfigurationParameters(Pipeline reconfiguredPipeline,
                                                                PipelineElementReconfigurationEntity reconfigurationEntity){
        pipelineExecutor.setSecondaryPipeline(reconfiguredPipeline);
        pipelineExecutor.setReconfigurationEntity(reconfigurationEntity);
        return this;
    }

    public PipelineExecutorBuilder addPrepareMigrationStep(){
        pipelineExecutor.addStep(new PrepareMigrationStep(pipelineExecutor));
        return this;
    }

    public PipelineExecutorBuilder addGetStateStep(){
        pipelineExecutor.addStep(new GetStateStep(pipelineExecutor));
        return this;
    }

    public PipelineExecutorBuilder addStartRelaysStep(){
        pipelineExecutor.addStep(new StartRelaysStep(pipelineExecutor));
        return this;
    }

    public PipelineExecutorBuilder addStartGraphsAndAssociatedRelaysStep(){
        pipelineExecutor.addStep(new StartGraphsAndAssociatedRelaysStep(pipelineExecutor));
        return this;
    }

    public PipelineExecutorBuilder addStopGraphsAndAssociatedRelaysStep(){
        pipelineExecutor.addStep(new StopGraphsAndAssociatedRelaysStep(pipelineExecutor));
        return this;
    }

    public PipelineExecutorBuilder addStopRelaysStep(){
        pipelineExecutor.addStep(new StopRelaysStep(pipelineExecutor));
        return this;
    }

    public PipelineExecutorBuilder addStoreMigratedPipelineStep(){
        pipelineExecutor.addStep(new StoreMigratedPipelineStep(pipelineExecutor));
        return this;
    }

    public PipelineExecutorBuilder addReconfigureElementStep(){
        pipelineExecutor.addStep(new ReconfigureElementStep(pipelineExecutor));
        return this;
    }

    public PipelineExecutorBuilder addPreparePipelineStartStep(){
        pipelineExecutor.addStep(new PrepareStartPipelineStep(pipelineExecutor));
        return this;
    }

    public PipelineExecutorBuilder addStartPipelineStep(){
        pipelineExecutor.addStep(new StartPipelineStep(pipelineExecutor));
        return this;
    }

    public PipelineExecutorBuilder addStorePipelineStep(){
        pipelineExecutor.addStep(new StorePipelineStep(pipelineExecutor));
        return this;
    }

    public PipelineExecutorBuilder addStopPipelineStep(){
        pipelineExecutor.addStep(new StopPipelineStep(pipelineExecutor));
        return this;
    }

    public PipelineExecutor buildPipelineExecutor(){
        return this.pipelineExecutor;
    }
}
