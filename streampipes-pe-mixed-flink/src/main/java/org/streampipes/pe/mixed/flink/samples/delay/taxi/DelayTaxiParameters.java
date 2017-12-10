package org.streampipes.pe.mixed.flink.samples.delay.taxi;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class DelayTaxiParameters extends EventProcessorBindingParams {
    private String labelPropertyMapping;

    public DelayTaxiParameters(DataProcessorInvocation graph) {
        super(graph);
    }

    public DelayTaxiParameters(DataProcessorInvocation graph, String labelPropertyMapping) {
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
