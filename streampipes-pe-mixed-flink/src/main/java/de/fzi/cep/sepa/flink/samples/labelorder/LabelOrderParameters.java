package de.fzi.cep.sepa.flink.samples.labelorder;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class LabelOrderParameters extends BindingParameters {

    public LabelOrderParameters(SepaInvocation graph) {
        super(graph);
    }
}
