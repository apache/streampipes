package de.fzi.cep.sepa.flink.samples.delay.sensor;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class DelayParameters extends BindingParameters {
    private int delayValue;
    private String labelPropertyMapping;

    public DelayParameters(SepaInvocation graph) {
        super(graph);
    }

    public DelayParameters(SepaInvocation graph, int delayValue, String labelPropertyMapping) {
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
