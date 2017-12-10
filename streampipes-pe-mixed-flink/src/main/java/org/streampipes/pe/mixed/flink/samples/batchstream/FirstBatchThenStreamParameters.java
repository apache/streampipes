package org.streampipes.pe.mixed.flink.samples.batchstream;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

/**
 * Created by philippzehnder on 16.02.17.
 */
public class FirstBatchThenStreamParameters extends EventProcessorBindingParams {
    public FirstBatchThenStreamParameters(DataProcessorInvocation graph) {
        super(graph);
    }
}
