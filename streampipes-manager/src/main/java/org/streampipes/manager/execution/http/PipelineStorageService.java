package org.streampipes.manager.execution.http;

import org.streampipes.manager.data.PipelineGraph;
import org.streampipes.manager.data.PipelineGraphBuilder;
import org.streampipes.manager.matching.InvocationGraphBuilder;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.storage.controller.StorageManager;

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

        StorageManager.INSTANCE.getPipelineStorageAPI().store(pipeline);
    }

    private <T> List<T> filter(List<InvocableStreamPipesEntity> graphs, Class<T> clazz) {
        return graphs
                .stream()
                .filter(graph -> clazz.isInstance(graph))
                .map(graph -> clazz.cast(graph))
                .collect(Collectors.toList());
    }
}
