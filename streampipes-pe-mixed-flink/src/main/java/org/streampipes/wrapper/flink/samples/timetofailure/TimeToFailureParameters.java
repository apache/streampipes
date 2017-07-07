package org.streampipes.wrapper.flink.samples.timetofailure;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.BindingParameters;

/**
 * Created by riemer on 26.10.2016.
 */
public class TimeToFailureParameters extends BindingParameters {

    private String healthIndexMapping;
    private Integer mtbfValue;

    public TimeToFailureParameters(SepaInvocation graph, String healthIndexMapping, Integer mtbfValue) {
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
