package org.streampipes.pe.mixed.flink.samples.healthindex;

import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

/**
 * Created by riemer on 17.10.2016.
 */
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
                .apply(new HealthIndexCalculator2(params.getFrictionMapping(), params.getTimestampMapping(), params.getMachineTypeMapping(), params.getHealthIndexVariables()));
    }
}
