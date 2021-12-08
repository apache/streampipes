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
package org.apache.streampipes.manager.execution.pipeline.executor.utils;

import org.apache.streampipes.manager.execution.http.GraphSubmitter;
import org.apache.streampipes.manager.execution.http.StateSubmitter;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementStatus;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

import java.util.ArrayList;
import java.util.List;

public class CommunicationUtils {

    public static PipelineOperationStatus startPipelineElementsAndRelays(List<InvocableStreamPipesEntity> graphs,
                                                                         List<SpDataStreamRelayContainer> relays,
                                                                         Pipeline pipeline){
        if (graphs.isEmpty()) {
            return StatusUtils.initPipelineOperationStatus(pipeline);
        }
        return new GraphSubmitter(pipeline.getPipelineId(), pipeline.getName(),
                graphs, new ArrayList<>(), relays).invokePipelineElementsAndRelays();
    }

    public static PipelineOperationStatus stopPipelineElementsAndRelays(List<InvocableStreamPipesEntity> graphs,
                                                                        List<SpDataStreamRelayContainer> relays,
                                                                        Pipeline pipeline){
        if (graphs.isEmpty()) {
            return StatusUtils.initPipelineOperationStatus(pipeline);
        }
        return new GraphSubmitter(pipeline.getPipelineId(), pipeline.getName(),
                graphs, new ArrayList<>(),relays).detachPipelineElementsAndRelays();
    }

    public static PipelineOperationStatus startRelays(List<SpDataStreamRelayContainer> relays, Pipeline pipeline){
        return new GraphSubmitter(pipeline.getPipelineId(), pipeline.getName(), new ArrayList<>(), new ArrayList<>(),
                relays).invokeRelaysOnMigration();
    }

    public static PipelineOperationStatus stopRelays(List<SpDataStreamRelayContainer> relays, Pipeline pipeline){
        return new GraphSubmitter(pipeline.getPipelineId(), pipeline.getName(),new ArrayList<>(), new ArrayList<>(),
                relays).detachRelaysOnMigration();
    }

    public static PipelineElementStatus getState(InvocableStreamPipesEntity graph, Pipeline pipeline){
        return new StateSubmitter(pipeline.getPipelineId(), pipeline.getName(), graph, null).getElementState();
    }

    public static PipelineElementStatus setState(InvocableStreamPipesEntity graph, String state, Pipeline pipeline){
        return new StateSubmitter(pipeline.getPipelineId(), pipeline.getName(), graph, state).setElementState();
    }

}
