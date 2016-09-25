package de.fzi.cep.sepa.manager.data;

import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.impl.EventStream;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by riemer on 23.09.2016.
 */
public class PipelineGraphHelpers {

    public static List<EventStream> findStreams(PipelineGraph pipelineGraph) {
        return find(pipelineGraph, EventStream.class);
    }

    public static List<InvocableSEPAElement> findInvocableElements(PipelineGraph pipelineGraph) {
        return find(pipelineGraph, InvocableSEPAElement.class);
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
