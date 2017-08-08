package org.streampipes.wrapper.flink.samples.labelorder;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.BindingParameters;

public class LabelOrderParameters extends BindingParameters {

    public LabelOrderParameters(SepaInvocation graph) {
        super(graph);
    }
}
