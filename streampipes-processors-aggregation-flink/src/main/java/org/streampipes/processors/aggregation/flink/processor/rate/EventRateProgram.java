/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.processors.aggregation.flink.processor.rate;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.streampipes.processors.aggregation.flink.AbstractAggregationProgram;

import java.util.HashMap;
import java.util.Map;

public class EventRateProgram extends AbstractAggregationProgram<EventRateParameter> {

  public EventRateProgram(EventRateParameter params, boolean debug) {
    super(params, debug);
    setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
  }

  @Override
  protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>... dataStreams) {
    return dataStreams[0]
            .timeWindowAll(Time.seconds(params.getAvgRate()))
            .apply(new EventRate(params.getAvgRate()))
            .flatMap(new FlatMapFunction<Float, Map<String, Object>>() {
              @Override
              public void flatMap(Float rate, Collector<Map<String, Object>> out) throws Exception {
                Map<String, Object> outMap = new HashMap<>();
                outMap.put("rate", rate);
                out.collect(outMap);
              }
            });
  }

}
