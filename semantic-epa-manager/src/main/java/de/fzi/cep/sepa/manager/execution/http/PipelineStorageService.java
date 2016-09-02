package de.fzi.cep.sepa.manager.execution.http;

import de.fzi.cep.sepa.commons.GenericTree;
import de.fzi.cep.sepa.manager.matching.InvocationGraphBuilder;
import de.fzi.cep.sepa.manager.matching.TreeBuilder;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.storage.controller.StorageManager;

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
        GenericTree<NamedSEPAElement> tree = new TreeBuilder(pipeline).generateTree(false);
        InvocationGraphBuilder builder = new InvocationGraphBuilder(tree, false, pipeline.getPipelineId());
        List<InvocableSEPAElement> graphs = builder.buildGraph();

        SecInvocation sec = getSECInvocationGraph(graphs);

        List<SepaInvocation> sepas = graphs
                .stream()
                .filter(graph -> graph instanceof SepaInvocation)
                .map(graph -> (SepaInvocation) graph)
                .collect(Collectors.toList());

        pipeline.setSepas(sepas);
        pipeline.setAction(sec);

        StorageManager.INSTANCE.getPipelineStorageAPI().store(pipeline);
    }

    private SecInvocation getSECInvocationGraph(List<InvocableSEPAElement> graphs)
    {
        for (InvocableSEPAElement graph : graphs)
            if (graph instanceof SecInvocation) return (SecInvocation) graph;
        throw new IllegalArgumentException("No action element available");
    }
}
