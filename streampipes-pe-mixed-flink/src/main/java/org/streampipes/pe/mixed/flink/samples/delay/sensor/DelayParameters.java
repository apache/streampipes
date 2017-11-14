package org.streampipes.pe.mixed.flink.samples.delay.sensor;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class DelayParameters extends EventProcessorBindingParams {
    private int delayValue;
    private String labelPropertyMapping;

    public DelayParameters(DataProcessorInvocation graph) {
        super(graph);
    }

    public DelayParameters(DataProcessorInvocation graph, int delayValue, String labelPropertyMapping) {
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
