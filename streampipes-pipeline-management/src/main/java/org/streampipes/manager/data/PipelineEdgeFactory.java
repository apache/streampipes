package org.streampipes.manager.data;

import org.streampipes.model.base.NamedStreamPipesEntity;
import org.jgrapht.EdgeFactory;

public class PipelineEdgeFactory implements EdgeFactory<NamedStreamPipesEntity, String> {

    @Override
    public String createEdge(NamedStreamPipesEntity sourceVertex, NamedStreamPipesEntity targetVertex) {
        return sourceVertex.getDOM() +"-" +targetVertex.getDOM();
    }
}
