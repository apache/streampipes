package org.streampipes.pe.mixed.flink.samples.labelorder;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.BindingParameters;

public class LabelOrderParameters extends BindingParameters {

    public LabelOrderParameters(SepaInvocation graph) {
        super(graph);
    }
}
