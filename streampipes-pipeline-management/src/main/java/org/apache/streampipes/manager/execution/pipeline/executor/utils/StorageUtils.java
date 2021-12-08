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

import org.apache.streampipes.manager.util.TemporaryGraphStorage;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.storage.api.INodeDataStreamRelay;
import org.apache.streampipes.storage.api.IPipelineStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.Date;
import java.util.List;

public class StorageUtils {

    public static void setPipelineStarted(Pipeline pipeline) {
        pipeline.setRunning(true);
        pipeline.setStartedAt(new Date().getTime());
        getPipelineStorageApi().updatePipeline(pipeline);
    }

    public static void setPipelineStopped(Pipeline pipeline) {
        pipeline.setRunning(false);
        getPipelineStorageApi().updatePipeline(pipeline);
    }

    public static void deleteVisualization(String pipelineId) {
        StorageDispatcher.INSTANCE
                .getNoSqlStore()
                .getVisualizationStorageApi()
                .deleteVisualization(pipelineId);
    }

    public static void storeInvocationGraphs(String pipelineId, List<InvocableStreamPipesEntity> graphs,
                                             List<SpDataSet> dataSets) {
        TemporaryGraphStorage.graphStorage.put(pipelineId, graphs);
        TemporaryGraphStorage.datasetStorage.put(pipelineId, dataSets);
    }

    public static void storeDataStreamRelayContainer(List<SpDataStreamRelayContainer> relays) {
        //relays.forEach(StreamPipesClusterManager::persistDataStreamRelay);
        relays.forEach(relay -> getDataStreamRelayApi().addRelayContainer(relay));
    }

    public static void deleteDataStreamRelayContainer(List<SpDataStreamRelayContainer> relays) {
        //relays.forEach(StreamPipesClusterManager::deleteDataStreamRelay);
        relays.forEach(relay -> getDataStreamRelayApi().deleteRelayContainer(relay));
    }

    public static void updateDataStreamRelayContainer(List<SpDataStreamRelayContainer> relays) {
        //relays.forEach(StreamPipesClusterManager::updateDataStreamRelay);
        relays.forEach(relay -> getDataStreamRelayApi().updateRelayContainer(relay));
    }



    /**
     * Get data stream relay storage dispatcher API
     *
     * @return INodeDataStreamRelay NoSQL storage interface for data stream relays
     */
    private static INodeDataStreamRelay getDataStreamRelayApi() {
        return StorageDispatcher.INSTANCE.getNoSqlStore().getNodeDataStreamRelayStorage();
    }

    /**
     * Get pipeline storage dispatcher API
     *
     * @return IPipelineStorage NoSQL storage interface for pipelines
     */
    private static IPipelineStorage getPipelineStorageApi() {
        return StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI();
    }


}
