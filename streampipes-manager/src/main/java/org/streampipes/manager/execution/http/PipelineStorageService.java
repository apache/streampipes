package org.streampipes.manager.execution.http;

import org.streampipes.manager.data.PipelineGraph;
import org.streampipes.manager.data.PipelineGraphBuilder;
import org.streampipes.manager.matching.InvocationGraphBuilder;
import org.streampipes.model.InvocableSEPAElement;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.storage.controller.StorageManager;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by riemer on 02.09.2016.
 */
public class PipelineStorageService {

    private Pipeline pipeline;

    public PipelineStorageService(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public void addPipeline() {
        PipelineGraph pipelineGraph = new PipelineGraphBuilder(pipeline).buildGraph();
        InvocationGraphBuilder builder = new InvocationGraphBuilder(pipelineGraph, pipeline.getPipelineId());
        List<InvocableSEPAElement> graphs = builder.buildGraphs();

        List<SecInvocation> secs = filter(graphs, SecInvocation.class);
        List<SepaInvocation> sepas = filter(graphs, SepaInvocation.class);

        pipeline.setSepas(sepas);
        pipeline.setActions(secs);

        StorageManager.INSTANCE.getPipelineStorageAPI().store(pipeline);
    }

    private <T> List<T> filter(List<InvocableSEPAElement> graphs, Class<T> clazz) {
        return graphs
                .stream()
                .filter(graph -> clazz.isInstance(graph))
                .map(graph -> clazz.cast(graph))
                .collect(Collectors.toList());
    }
}
