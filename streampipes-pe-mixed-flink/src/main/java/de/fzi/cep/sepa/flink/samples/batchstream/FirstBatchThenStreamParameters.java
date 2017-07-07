package de.fzi.cep.sepa.flink.samples.batchstream;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

/**
 * Created by philippzehnder on 16.02.17.
 */
public class FirstBatchThenStreamParameters extends BindingParameters {
    public FirstBatchThenStreamParameters(SepaInvocation graph) {
        super(graph);
    }
}
