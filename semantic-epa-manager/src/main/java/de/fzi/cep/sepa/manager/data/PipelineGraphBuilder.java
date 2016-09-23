package de.fzi.cep.sepa.manager.data;

import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 23.09.2016.
 */
public class PipelineGraphBuilder {

    private Pipeline pipeline;
    private List<NamedSEPAElement> allPipelineElements;

    public PipelineGraphBuilder(Pipeline pipeline) {
        this.pipeline = pipeline;
        this.allPipelineElements = addAll();
    }

    private List<NamedSEPAElement> addAll() {
        List<NamedSEPAElement> allElements = new ArrayList<>();
        allElements.addAll(pipeline.getStreams());
        allElements.addAll(pipeline.getSepas());
        allElements.addAll(pipeline.getActions());
    }


    public PipelineGraph buildGraph() {
        PipelineGraph pipelineGraph = new PipelineGraph();

        //pipelineGraph.

        return null;
    }

    private List<NamedSEPAElement> findChildren(NamedSEPAElement element) {

    }

}
