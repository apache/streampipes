/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.pe.flink.processor.sequence;

import org.apache.streampipes.model.runtime.Event;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.util.Collector;

public class Sequence extends CoProcessFunction<Event, Event, Event> {

  private String timeUnit;
  private Integer timeWindow;

  private ValueState<EventStorage> state;

  public Sequence(String timeUnit, Integer timeWindow) {
    this.timeUnit = timeUnit;
    this.timeWindow = timeWindow;
  }

  //@Override
  //public void open(Configuration parameters) throws Exception {
  // TODO: add RuntimeContext
  //state = getRuntimeContext().getState(new ValueStateDescriptor<>("sequence-event-storage",
  //        EventStorage.class));
  //}


  @Override
  public void processElement1(Event value, Context ctx, Collector<Event> out) throws Exception {
    state.update(new EventStorage(System.currentTimeMillis(), value));
  }

  @Override
  public void processElement2(Event value, Context ctx, Collector<Event> out) throws Exception {
    EventStorage previousElementStream1 = state.value();
    if (previousElementStream1 != null && isSequence(previousElementStream1, value)) {
      previousElementStream1.getEvent().getFields().forEach((key, v) -> value.addField(v));
      out.collect(value);
    }
  }

  private Boolean isSequence(EventStorage previousElementStream1, Event value) {
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
  public void onTimer(long timestamp, OnTimerContext ctx, Collector<Event> out) throws Exception {

  }
}
