/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.manager.execution.http;

import org.streampipes.manager.data.PipelineGraph;
import org.streampipes.manager.data.PipelineGraphBuilder;
import org.streampipes.manager.matching.InvocationGraphBuilder;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.storage.management.StorageDispatcher;

import java.util.List;
import java.util.stream.Collectors;

public class PipelineStorageService {

    private Pipeline pipeline;

    public PipelineStorageService(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public void addPipeline() {
        PipelineGraph pipelineGraph = new PipelineGraphBuilder(pipeline).buildGraph();
        InvocationGraphBuilder builder = new InvocationGraphBuilder(pipelineGraph, pipeline.getPipelineId());
        List<InvocableStreamPipesEntity> graphs = builder.buildGraphs();

        List<DataSinkInvocation> secs = filter(graphs, DataSinkInvocation.class);
        List<DataProcessorInvocation> sepas = filter(graphs, DataProcessorInvocation.class);

        pipeline.setSepas(sepas);
        pipeline.setActions(secs);

        StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().store(pipeline);
    }

    private <T> List<T> filter(List<InvocableStreamPipesEntity> graphs, Class<T> clazz) {
        return graphs
                .stream()
                .filter(graph -> clazz.isInstance(graph))
                .map(graph -> clazz.cast(graph))
                .collect(Collectors.toList());
    }
}
