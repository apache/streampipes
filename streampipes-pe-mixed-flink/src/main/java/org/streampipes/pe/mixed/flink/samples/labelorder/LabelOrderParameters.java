package org.streampipes.pe.mixed.flink.samples.labelorder;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class LabelOrderParameters extends EventProcessorBindingParams {

    public LabelOrderParameters(DataProcessorInvocation graph) {
        super(graph);
    }
}
