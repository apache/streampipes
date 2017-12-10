package org.streampipes.manager.data;

import org.streampipes.model.base.NamedStreamPipesEntity;
import org.jgrapht.EdgeFactory;

/**
 * Created by riemer on 23.09.2016.
 */
public class PipelineEdgeFactory implements EdgeFactory<NamedStreamPipesEntity, String> {

    @Override
    public String createEdge(NamedStreamPipesEntity sourceVertex, NamedStreamPipesEntity targetVertex) {
        return sourceVertex.getDOM() +"-" +targetVertex.getDOM();
    }
}
