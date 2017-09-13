package org.streampipes.pe.mixed.flink.samples.enrich.value;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.Map;

public class ValueEnricher implements FlatMapFunction<Map<String, Object>, Map<String, Object>>  {


    private String value = "";
    private String valueName = "";

    public ValueEnricher(String valueName, String value) {
        this.value = value;
        this.valueName = valueName;
    }

    @Override
    public void flatMap(Map<String, Object> in, Collector<Map<String, Object>> out) throws Exception {
        in.put(valueName, value);
        out.collect(in);
    }
}
