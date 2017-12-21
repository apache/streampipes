package org.streampipes.pe.mixed.flink.samples.timetofailure;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class TimeToFailureParameters extends EventProcessorBindingParams {

    private String healthIndexMapping;
    private Integer mtbfValue;

    public TimeToFailureParameters(DataProcessorInvocation graph, String healthIndexMapping, Integer mtbfValue) {
        super(graph);
        this.healthIndexMapping = healthIndexMapping;
        this.mtbfValue = mtbfValue;
    }

    public String getHealthIndexMapping() {
        return healthIndexMapping;
    }

    public void setHealthIndexMapping(String healthIndexMapping) {
        this.healthIndexMapping = healthIndexMapping;
    }

    public Integer getMtbfValue() {
        return mtbfValue;
    }

    public void setMtbfValue(Integer mtbfValue) {
        this.mtbfValue = mtbfValue;
    }
}
