package org.streampipes.manager.data;

import org.streampipes.model.base.NamedStreamPipesEntity;
import org.jgrapht.graph.SimpleDirectedGraph;

public class PipelineGraph extends SimpleDirectedGraph<NamedStreamPipesEntity, String> {

    public PipelineGraph() {
        super(new PipelineEdgeFactory());
    }
}
