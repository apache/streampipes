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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.manager.execution.pipeline.executor.operations.*;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementMigrationEntity;
import org.apache.streampipes.model.pipeline.PipelineElementReconfigurationEntity;

public class PipelineExecutorBuilder {

    private PipelineExecutor pipelineExecutor;

    private boolean reconfigurationParametersSet = false;

    private boolean migrationParametersSet = false;

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
        this.migrationParametersSet = true;
        return this;
    }

    public PipelineExecutorBuilder setReconfigurationParameters(Pipeline reconfiguredPipeline,
                                                                PipelineElementReconfigurationEntity reconfigurationEntity){
        pipelineExecutor.setSecondaryPipeline(reconfiguredPipeline);
        pipelineExecutor.setReconfigurationEntity(reconfigurationEntity);
        this.reconfigurationParametersSet = true;
        return this;
    }

    public PipelineExecutorBuilder addPrepareMigrationOperation(){
        pipelineExecutor.addOperation(new PrepareMigrationOperation(pipelineExecutor));
        return this;
    }

    public PipelineExecutorBuilder addGetStateOperation(){
        pipelineExecutor.addOperation(new GetStateOperation(pipelineExecutor));
        return this;
    }

    public PipelineExecutorBuilder addStartRelaysFromPredecessorsOperation(){
        pipelineExecutor.addOperation(new StartRelaysFromPredecessorsOperation(pipelineExecutor));
        return this;
    }

    public PipelineExecutorBuilder addStartTargetPipelineElementsOperation(){
        pipelineExecutor.addOperation(new StartTargetPipelineElementsOperation(pipelineExecutor));
        return this;
    }

    public PipelineExecutorBuilder addStopOriginPipelineElementAndRelaysOperation(){
        pipelineExecutor.addOperation(new StopOriginPipelineElementAndRelaysOperation(pipelineExecutor));
        return this;
    }

    public PipelineExecutorBuilder addStopRelaysFromPredecessorOperation(){
        pipelineExecutor.addOperation(new StopRelaysFromPredecessorOperation(pipelineExecutor));
        return this;
    }

    public PipelineExecutorBuilder addStoreMigratedPipelineOperation(){
        pipelineExecutor.addOperation(new StoreMigratedPipelineOperation(pipelineExecutor));
        return this;
    }

    public PipelineExecutorBuilder addReconfigureElementOperation(){
        pipelineExecutor.addOperation(new ReconfigureElementOperation(pipelineExecutor));
        return this;
    }

    public PipelineExecutorBuilder addStartPipelineOperation(){
        pipelineExecutor.addOperation(new StartPipelineOperation(pipelineExecutor));
        return this;
    }

    public PipelineExecutorBuilder addStorePipelineOperation(){
        pipelineExecutor.addOperation(new StorePipelineOperation(pipelineExecutor));
        return this;
    }

    public PipelineExecutorBuilder addStopPipelineOperation(){
        pipelineExecutor.addOperation(new StopPipelineOperation(pipelineExecutor));
        return this;
    }

    public PipelineExecutor buildPipelineExecutor(){
        //Is this check needed? Only relevant for core development not for users but gives a little more clarity at the
        //cost of introducing some new boolean flags and marker interfaces
        if((this.pipelineExecutor.containsMigrationOperation() && !this.migrationParametersSet)
                || (this.pipelineExecutor.containsReconfigurationOperation() && !this.reconfigurationParametersSet))
            throw new SpRuntimeException("PipelineExecutor can't be build since the required parameters have not been set");
        return this.pipelineExecutor;    }

}
