package org.streampipes.wrapper.flink.samples.timetofailure;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.Map;

/**
 * Created by riemer on 26.10.2016.
 */
public class TimeToFailureCalculator implements FlatMapFunction<Map<String, Object>, Map<String, Object>> {

    private String healthIndexMapping;
    private Integer mtbfValue;

    public TimeToFailureCalculator(String healthIndexMapping, Integer mtbfValue) {
        this.healthIndexMapping = healthIndexMapping;
        this.mtbfValue = mtbfValue;
    }

    @Override
    public void flatMap(Map<String, Object> inMap, Collector<Map<String, Object>> collector) throws Exception {
        inMap.put("ttf", calculateTtf((Double) inMap.get(healthIndexMapping)));
        collector.collect(inMap);
    }

    private Double calculateTtf(Double healthIndex) {
        System.out.println("TTF: " +mtbfValue * healthIndex);
        return mtbfValue * healthIndex;
    }
}
