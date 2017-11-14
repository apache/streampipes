package org.streampipes.manager.data;

import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.SpDataStream;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by riemer on 23.09.2016.
 */
public class PipelineGraphHelpers {

    public static List<SpDataStream> findStreams(PipelineGraph pipelineGraph) {
        return find(pipelineGraph, SpDataStream.class);
    }

    public static List<InvocableStreamPipesEntity> findInvocableElements(PipelineGraph pipelineGraph) {
        return find(pipelineGraph, InvocableStreamPipesEntity.class);
    }

    private static <T> List<T> find(PipelineGraph pipelineGraph, Class<T> clazz) {
        return pipelineGraph
                .vertexSet()
                .stream()
                .filter(v -> clazz.isInstance(v))
                .map(s -> clazz.cast(s))
                .collect(Collectors.toList());
    }

}
