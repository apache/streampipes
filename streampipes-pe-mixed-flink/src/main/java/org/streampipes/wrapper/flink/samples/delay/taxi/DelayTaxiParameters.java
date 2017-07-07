package org.streampipes.wrapper.flink.samples.delay.taxi;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.runtime.BindingParameters;

public class DelayTaxiParameters extends BindingParameters {
    private String labelPropertyMapping;

    public DelayTaxiParameters(SepaInvocation graph) {
        super(graph);
    }

    public DelayTaxiParameters(SepaInvocation graph, String labelPropertyMapping) {
        super(graph);
        this.labelPropertyMapping = labelPropertyMapping;
    }

    public String getLabelPropertyMapping() {
        return labelPropertyMapping;
    }

    public void setLabelPropertyMapping(String labelPropertyMapping) {
        this.labelPropertyMapping = labelPropertyMapping;
    }
}
