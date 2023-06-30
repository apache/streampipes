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

package org.apache.streampipes.pe.flink.processor.aggregation;

import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorProgram;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregationProgram extends FlinkDataProcessorProgram<AggregationParameters> {

  public AggregationProgram(AggregationParameters params) {
    super(params);
    //setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
  }

  @Override
  public DataStream<Event> getApplicationLogic(DataStream<Event>... dataStreams) {
    return getKeyedStream(dataStreams[0]);
  }

  private DataStream<Event> getKeyedStream(DataStream<Event> dataStream) {
    if (params.getGroupBy().size() > 0) {
      KeyedStream<Event, Map<String, String>> keyedStream = dataStream.keyBy(getKeySelector());
      if (params.getTimeWindow()) {
        return keyedStream
            .window(SlidingEventTimeWindows.of(Time.seconds(params.getWindowSize()),
                Time.seconds(params.getOutputEvery())))
            .apply(new TimeAggregation(params.getAggregationType(), params.getAggregateKeyList(),
                params.getGroupBy()));
      } else {
        return keyedStream
            .countWindow(params.getWindowSize(), params.getOutputEvery())
            .apply(new CountAggregation(params.getAggregationType(), params.getAggregateKeyList(),
                params.getGroupBy()));
      }
    } else {
      if (params.getTimeWindow()) {
        return dataStream
            .timeWindowAll(Time.seconds(params.getWindowSize()), Time.seconds(params.getOutputEvery()))
            .apply(new TimeAggregation(params.getAggregationType(), params.getAggregateKeyList()));
      } else {
        return dataStream
            .countWindowAll(params.getWindowSize(), params.getOutputEvery())
            .apply(new CountAggregation(params.getAggregationType(), params.getAggregateKeyList()));
      }
    }
  }

  private KeySelector<Event, Map<String, String>> getKeySelector() {
    List<String> groupBy = params.getGroupBy();
    return new KeySelector<Event, Map<String, String>>() {
      @Override
      public Map<String, String> getKey(Event event) throws Exception {
        Map<String, String> keys = new HashMap<>();
        for (String groupBy : groupBy) {
          keys.put(groupBy, event.getFieldBySelector(groupBy).getAsPrimitive().getAsString());
        }
        return keys;
      }
    };
  }
}
