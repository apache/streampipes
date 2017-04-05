package de.fzi.cep.sepa.flink.samples.delay.taxi;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class DelayTaxiParameters extends BindingParameters {
    private int delayValue;
    private String labelPropertyMapping;

    public DelayTaxiParameters(SepaInvocation graph) {
        super(graph);
    }

    public DelayTaxiParameters(SepaInvocation graph, int delayValue, String labelPropertyMapping) {
        super(graph);
        this.delayValue = delayValue;
        this.labelPropertyMapping = labelPropertyMapping;
    }

    public int getDelayValue() {
        return delayValue;
    }

    public void setDelayValue(int delayValue) {
        this.delayValue = delayValue;
    }

    public String getLabelPropertyMapping() {
        return labelPropertyMapping;
    }

    public void setLabelPropertyMapping(String labelPropertyMapping) {
        this.labelPropertyMapping = labelPropertyMapping;
    }
}
