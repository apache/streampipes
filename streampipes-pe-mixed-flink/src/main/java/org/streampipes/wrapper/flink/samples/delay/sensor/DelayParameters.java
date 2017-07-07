package org.streampipes.wrapper.flink.samples.delay.sensor;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.runtime.BindingParameters;

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
