package org.streampipes.pe.mixed.flink.samples.healthindex;

import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

public class HealthIndexProgram extends FlinkDataProcessorRuntime<HealthIndexParameters> {

    public HealthIndexProgram(HealthIndexParameters params) {
        super(params);
    }

    public HealthIndexProgram(HealthIndexParameters params, FlinkDeploymentConfig config) {
        super(params, config);
    }


    @Override
    protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>... messageStream) {
        return messageStream[0]
                .countWindowAll(2, 1)
                .apply(new HealthIndexCalculator2(bindingParams.getFrictionMapping(),
                        bindingParams.getTimestampMapping(),
                        bindingParams.getMachineTypeMapping(),
                        bindingParams.getHealthIndexVariables()));
    }
}
