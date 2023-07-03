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
package org.apache.streampipes.pe.flink.processor.and;

import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.pe.flink.AbstractPatternDetectionProgram;

import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.util.List;


public class AndProgram extends AbstractPatternDetectionProgram<AndParameters> {

  public AndProgram(AndParameters params) {
    super(params);
  }

  @Override
  public DataStream<Event> getApplicationLogic(DataStream<Event>... messageStream) {
    // A AND B within x minutes
    List<String> leftMappings = params.getLeftMappings();
    List<String> rightMappings = params.getRightMappings();
    Time time = TimeUnitConverter.toTime(params.getTimeUnit(), params.getTimeWindow());

    return messageStream[0].join(messageStream[1])
        .where(new KeySelector<Event, String>() {
          @Override
          public String getKey(Event stringObjectMap) throws Exception {
            StringBuilder builder = new StringBuilder();
            for (String key : leftMappings) {
              builder.append(key);
            }
            return builder.toString();
          }
        }).equalTo(new KeySelector<Event, String>() {
          @Override
          public String getKey(Event stringObjectMap) throws Exception {
            StringBuilder builder = new StringBuilder();
            for (String key : rightMappings) {
              builder.append(key);
            }
            return builder.toString();
          }
        }).window(TumblingEventTimeWindows.of(time))
        .apply(new JoinFunction<Event, Event, Event>() {
          @Override
          public Event join(Event e1, Event e2) throws Exception {
            Event map = new Event();
            e1.getFields().forEach((key, value) -> map.addField(value));
            e2.getFields().forEach((key, value) -> map.addField(value));
            return map;
          }
        });
  }
}
