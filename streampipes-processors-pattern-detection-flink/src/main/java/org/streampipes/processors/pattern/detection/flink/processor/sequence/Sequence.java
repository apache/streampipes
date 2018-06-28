/*
 * Copyright 2017 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.streampipes.processors.pattern.detection.flink.processor.sequence;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.util.Collector;

import java.util.Map;

public class Sequence extends CoProcessFunction<Map<String, Object>, Map<String, Object>, Map<String, Object>> {

  private String timeUnit;
  private Integer timeWindow;

  private ValueState<EventStorage> state;

  public Sequence(String timeUnit, Integer timeWindow) {
    this.timeUnit = timeUnit;
    this.timeWindow = timeWindow;
  }

  @Override
  public void open(Configuration parameters) throws Exception {
    state = getRuntimeContext().getState(new ValueStateDescriptor<>("sequence-event-storage", EventStorage.class));
  }


  @Override
  public void processElement1(Map<String, Object> value, Context ctx, Collector<Map<String, Object>> out) throws Exception {
    state.update(new EventStorage(System.currentTimeMillis(), value));
  }

  @Override
  public void processElement2(Map<String, Object> value, Context ctx, Collector<Map<String, Object>> out) throws Exception {
    EventStorage previousElementStream1 = state.value();
    if (previousElementStream1 != null && isSequence(previousElementStream1, value)) {
      value.putAll(previousElementStream1.getEvent());
      out.collect(value);
    }
  }

  private Boolean isSequence(EventStorage previousElementStream1, Map<String, Object> value) {
    Long currentTime = System.currentTimeMillis();
    Long earliestAllowedStartTime = getEarliestStartTime(currentTime);

    return previousElementStream1.getTimestamp() >= earliestAllowedStartTime;
  }

  private Long getEarliestStartTime(Long currentTime) {
    Integer multiplier;

    if (timeUnit.equals("sec")) {
      multiplier = 1000;
    } else if (timeUnit.equals("min")) {
      multiplier = 1000 * 60;
    } else {
      multiplier = 1000 * 60 * 60;
    }

    return currentTime - (multiplier * timeWindow);
  }

  @Override
  public void onTimer(long timestamp, OnTimerContext ctx, Collector<Map<String, Object>> out) throws Exception {

  }
}
