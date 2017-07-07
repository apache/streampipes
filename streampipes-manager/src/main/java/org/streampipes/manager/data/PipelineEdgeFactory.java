package org.streampipes.manager.data;

import org.streampipes.model.NamedSEPAElement;
import org.jgrapht.EdgeFactory;

/**
 * Created by riemer on 23.09.2016.
 */
public class PipelineEdgeFactory implements EdgeFactory<NamedSEPAElement, String> {

    @Override
    public String createEdge(NamedSEPAElement sourceVertex, NamedSEPAElement targetVertex) {
        return sourceVertex.getDOM() +"-" +targetVertex.getDOM();
    }
}
