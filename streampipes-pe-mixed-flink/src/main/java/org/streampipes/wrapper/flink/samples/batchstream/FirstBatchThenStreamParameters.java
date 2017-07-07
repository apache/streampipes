package org.streampipes.wrapper.flink.samples.batchstream;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.runtime.BindingParameters;

/**
 * Created by philippzehnder on 16.02.17.
 */
public class FirstBatchThenStreamParameters extends BindingParameters {
    public FirstBatchThenStreamParameters(SepaInvocation graph) {
        super(graph);
    }
}
