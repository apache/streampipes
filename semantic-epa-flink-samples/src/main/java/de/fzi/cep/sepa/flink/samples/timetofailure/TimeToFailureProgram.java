package de.fzi.cep.sepa.flink.samples.timetofailure;

import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

/**
 * Created by riemer on 26.10.2016.
 */
public class TimeToFailureProgram extends FlinkSepaRuntime<TimeToFailureParameters> {

    public TimeToFailureProgram(TimeToFailureParameters params) {
        super(params);
    }

    public TimeToFailureProgram(TimeToFailureParameters params, FlinkDeploymentConfig config) {
        super(params, config);
    }


    @Override
    protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>> messageStream) {
        return messageStream.flatMap(new TimeToFailureCalculator(params.getHealthIndexMapping()));
    }
}
