package org.streampipes.pe.mixed.flink.samples.breakdown;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.Map;

public class Prediction2BreakDownMapper implements FlatMapFunction<Map<String, Object>, Map<String, Object>>  {


  @Override
  public void flatMap(Map<String, Object> stringObjectMap, Collector<Map<String, Object>> collector) throws Exception {
    collector.collect(stringObjectMap);
  }
}
