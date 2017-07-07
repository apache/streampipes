package de.fzi.cep.sepa.manager.data;

import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.pipeline.Pipeline;
import de.fzi.cep.sepa.model.impl.EventStream;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by riemer on 23.09.2016.
 */
public class PipelineGraphBuilder {

    private Pipeline pipeline;
    private List<NamedSEPAElement> allPipelineElements;
    private List<InvocableSEPAElement> invocableElements;

    public PipelineGraphBuilder(Pipeline pipeline) {
        this.pipeline = pipeline;
        this.allPipelineElements = addAll();
        this.invocableElements = addInvocable();
    }

    private List<NamedSEPAElement> addAll() {
        List<NamedSEPAElement> allElements = new ArrayList<>();
        allElements.addAll(pipeline.getStreams());
        allElements.addAll(addInvocable());
        return allElements;
    }

    private List<InvocableSEPAElement> addInvocable() {
        List<InvocableSEPAElement> allElements = new ArrayList<>();
        allElements.addAll(pipeline.getSepas());
        allElements.addAll(pipeline.getActions());
        return allElements;
    }


    public PipelineGraph buildGraph() {
        PipelineGraph pipelineGraph = new PipelineGraph();
        allPipelineElements.forEach(p -> pipelineGraph.addVertex(p));

        for(NamedSEPAElement source : allPipelineElements) {
            List<InvocableSEPAElement> targets = findTargets(source.getDOM());
            targets.forEach(t -> pipelineGraph.addEdge(source, t));
        }

        return pipelineGraph;
    }

    private List<InvocableSEPAElement> findTargets(String domId) {
        return invocableElements
                .stream()
                .filter(i -> i.getConnectedTo().contains(domId))
                .collect(Collectors.toList());
    }

}
