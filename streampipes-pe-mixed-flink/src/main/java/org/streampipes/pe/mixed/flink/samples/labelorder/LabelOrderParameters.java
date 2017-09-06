package org.streampipes.pe.mixed.flink.samples.labelorder;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class LabelOrderParameters extends EventProcessorBindingParams {

    public LabelOrderParameters(SepaInvocation graph) {
        super(graph);
    }
}
