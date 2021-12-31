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

import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementMigrationEntity;
import org.apache.streampipes.model.pipeline.PipelineElementReconfigurationEntity;

public class PipelineExecutorFactory {

    public static PipelineExecutor createMigrationExecutor(Pipeline pipeline, boolean visualize, boolean storeStatus,
                                                           boolean monitor, Pipeline pipelineBeforeMigration,
                                                           PipelineElementMigrationEntity migrationEntity){
        PipelineExecutorBuilder builder = PipelineExecutorBuilder.getBuilder()
                .initializePipelineExecutor(pipeline, visualize, storeStatus, monitor)
                .setMigrationParameters(pipelineBeforeMigration, migrationEntity)
                .addPrepareMigrationStep();
        if(migrationEntity.getTargetElement().isStateful())
            builder.addGetStateStep();
        builder.addStartGraphsAndAssociatedRelaysStep()
                .addStopRelaysStep()
                .addStartRelaysStep()
                .addStopGraphsAndAssociatedRelaysStep()
                .addStoreMigratedPipelineStep();
        return builder.buildPipelineExecutor();
    }

    public static PipelineExecutor createReconfigurationExecutor(Pipeline pipeline, boolean visualize, boolean storeStatus,
                                                                 boolean monitor, Pipeline reconfiguredPipeline,
                                                                 PipelineElementReconfigurationEntity reconfigurationEntity){
        return PipelineExecutorBuilder.getBuilder().initializePipelineExecutor(pipeline, visualize, storeStatus, monitor)
                .setReconfigurationParameters(reconfiguredPipeline, reconfigurationEntity).addReconfigureElementStep()
                .buildPipelineExecutor();
    }

    public static PipelineExecutor createInvocationExecutor(Pipeline pipeline, boolean visualize, boolean storeStatus,
                                                            boolean monitor){
        return PipelineExecutorBuilder.getBuilder().initializePipelineExecutor(pipeline, visualize, storeStatus, monitor)
                .addPreparePipelineStartStep()
                .addStartPipelineStep()
                .addStorePipelineStep()
                .buildPipelineExecutor();
    }

    public static PipelineExecutor createDetachExecutor(Pipeline pipeline, boolean visualize, boolean storeStatus,
                                                        boolean monitor){
        return PipelineExecutorBuilder.getBuilder().initializePipelineExecutor(pipeline, visualize, storeStatus, monitor)
                .addStopPipelineStep().buildPipelineExecutor();
    }

}
