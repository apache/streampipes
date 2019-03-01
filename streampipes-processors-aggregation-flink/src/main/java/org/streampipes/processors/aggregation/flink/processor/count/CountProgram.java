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
package org.streampipes.processors.aggregation.flink.processor.count;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.streampipes.model.runtime.Event;
import org.streampipes.processors.aggregation.flink.AbstractAggregationProgram;

import java.util.Map;

public class CountProgram extends AbstractAggregationProgram<CountParameters> {

  public CountProgram(CountParameters params, boolean debug) {
    super(params, debug);
    setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
  }

  @Override
  protected DataStream<Event> getApplicationLogic(DataStream<Event>... dataStreams) {
    return dataStreams[0]
            .map(new CountMapper(params.getFieldToCount()))
            .keyBy(1)
            .timeWindow(params.getTime())
            .trigger(new Trigger<Tuple3<String, String, Integer>, TimeWindow>() {
              @Override
              public TriggerResult onElement(Tuple3<String, String, Integer> stringStringIntegerTuple3, long l, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
                return TriggerResult.FIRE;
              }

              @Override
              public TriggerResult onProcessingTime(long l, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
                return TriggerResult.CONTINUE;
              }

              @Override
              public TriggerResult onEventTime(long l, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
                return TriggerResult.CONTINUE;
              }

              @Override
              public void clear(TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {

              }
            })
            .sum(2)
            .map(new Tuple2MapMapper());
//            .map()
//            .map(new MapFunction<Map<String, Object>, Map<String, Object>>() {
//              @Override
//              public Map<String, Object> map(Map<String, Object> stringObjectMap) throws Exception {
//                Map<String, Object> outMap = new HashMap<>();
//              outMap.put("value", params.getFieldToCount());
//              outMap.put("count", "");
//              return outMap;
//              }
//            });
//            .apply((WindowFunction<String, Map<String, Object>, Map<String, Object>, TimeWindow>) (map, timeWindow, iterable, collector) -> {
//              Integer count = 0;
//              Iterator<String> it = iterable.iterator();
//              while(it.hasNext()) {
//                String next = it.next();
//                if (next.equals(map.get(params.getFieldToCount()))) {
//                  count++;
//                }
//              }
//
//              Map<String, Object> outMap = new HashMap<>();
//              outMap.put("value", map.get(params.getFieldToCount()));
//              outMap.put("count", count);
//              collector.collect(outMap);
//            });


  }

  @Override
  public void appendEnvironmentConfig(StreamExecutionEnvironment env) {
    env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
  }

}
