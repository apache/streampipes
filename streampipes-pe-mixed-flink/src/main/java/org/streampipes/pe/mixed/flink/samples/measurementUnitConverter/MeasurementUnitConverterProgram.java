package org.streampipes.pe.mixed.flink.samples.measurementUnitConverter;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;

import java.util.Map;

public class MeasurementUnitConverterProgram extends FlinkSepaRuntime<MeasurementUnitConverterParameters> {

    public MeasurementUnitConverterProgram(MeasurementUnitConverterParameters params) {
        super(params);
    }

    public MeasurementUnitConverterProgram(MeasurementUnitConverterParameters params, FlinkDeploymentConfig config) {
        super(params, config);
    }

    @Override
    protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>... dataStreams) {
        return dataStreams[0].flatMap(new MeasurementUnitConverter(params));
    }
}
