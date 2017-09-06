package org.streampipes.pe.mixed.flink.samples.healthindex;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

/**
 * Created by riemer on 17.10.2016.
 */
public class HealthIndexParameters extends EventProcessorBindingParams {

    private String frictionMapping;
    private String machineTypeMapping;
    private String timestampMapping;

    private HealthIndexVariables2 healthIndexVariables;

    public HealthIndexParameters(SepaInvocation graph, String frictionMapping, String timestampMapping, String machineTypeMapping, HealthIndexVariables2 healthIndexVariables) {
        super(graph);
        this.frictionMapping = frictionMapping;
        this.healthIndexVariables = healthIndexVariables;
        this.machineTypeMapping = machineTypeMapping;
        this.timestampMapping = timestampMapping;
    }


    public String getFrictionMapping() {
        return frictionMapping;
    }

    public void setFrictionMapping(String frictionMapping) {
        this.frictionMapping = frictionMapping;
    }

    public HealthIndexVariables2 getHealthIndexVariables() {
        return healthIndexVariables;
    }

    public void setHealthIndexVariables(HealthIndexVariables2 healthIndexVariables) {
        this.healthIndexVariables = healthIndexVariables;
    }

    public String getMachineTypeMapping() {
        return machineTypeMapping;
    }

    public void setMachineTypeMapping(String machineTypeMapping) {
        this.machineTypeMapping = machineTypeMapping;
    }

    public String getTimestampMapping() {
        return timestampMapping;
    }

    public void setTimestampMapping(String timestampMapping) {
        this.timestampMapping = timestampMapping;
    }
}
