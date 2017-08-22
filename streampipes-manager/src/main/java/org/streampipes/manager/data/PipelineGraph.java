package org.streampipes.manager.data;

import org.streampipes.model.NamedSEPAElement;
import org.jgrapht.graph.SimpleDirectedGraph;

/**
 * Created by riemer on 23.09.2016.
 */
public class PipelineGraph extends SimpleDirectedGraph<NamedSEPAElement, String> {

    public PipelineGraph() {
        super(new PipelineEdgeFactory());
    }
}
