package org.streampipes.pe.mixed.flink.samples.timetofailure;

import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

public class TimeToFailureProgram extends FlinkDataProcessorRuntime<TimeToFailureParameters> {

    public TimeToFailureProgram(TimeToFailureParameters params) {
        super(params);
    }

    public TimeToFailureProgram(TimeToFailureParameters params, FlinkDeploymentConfig config) {
        super(params, config);
    }


    @Override
    protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>... messageStream) {
        return messageStream[0].flatMap(new TimeToFailureCalculator(params.getHealthIndexMapping(), params.getMtbfValue()));
    }
}
