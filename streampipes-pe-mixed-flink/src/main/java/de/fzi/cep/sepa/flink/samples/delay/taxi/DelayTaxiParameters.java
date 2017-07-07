package de.fzi.cep.sepa.flink.samples.delay.taxi;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

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
