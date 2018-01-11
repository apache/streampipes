package org.streampipes.pe.mixed.flink.samples.batchstream;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class FirstBatchThenStreamParameters extends EventProcessorBindingParams {
    public FirstBatchThenStreamParameters(DataProcessorInvocation graph) {
        super(graph);
    }
}
