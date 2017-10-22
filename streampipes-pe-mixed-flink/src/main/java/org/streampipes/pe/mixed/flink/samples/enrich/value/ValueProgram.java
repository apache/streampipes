package org.streampipes.pe.mixed.flink.samples.enrich.value;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;

import java.util.Map;

public class ValueProgram extends FlinkSepaRuntime<ValueParameters> {

    public ValueProgram(ValueParameters params) {
        super(params);
    }

    public ValueProgram(ValueParameters params, FlinkDeploymentConfig config) {
        super(params, config);
    }

    @Override
    protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>... dataStreams) {
        return dataStreams[0].flatMap(new ValueEnricher(getParams().getValueName(), params.getValue()));
    }
}
