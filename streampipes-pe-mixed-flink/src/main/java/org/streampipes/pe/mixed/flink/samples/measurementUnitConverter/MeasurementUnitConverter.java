package org.streampipes.pe.mixed.flink.samples.measurementUnitConverter;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.Map;

public class MeasurementUnitConverter implements FlatMapFunction<Map<String, Object>, Map<String, Object>>  {

    @Override
    public void flatMap(Map<String, Object> in, Collector<Map<String, Object>> out) throws Exception {
        out.collect(in);
    }
}
