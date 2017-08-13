package org.streampipes.wrapper.flink.samples.batchstream;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

/**
 * Created by philippzehnder on 16.02.17.
 */
public class FirstBatchThenStreamParameters extends EventProcessorBindingParams {
    public FirstBatchThenStreamParameters(SepaInvocation graph) {
        super(graph);
    }
}
