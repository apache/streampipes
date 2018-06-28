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

package org.streampipes.processors.aggregation.flink.processor.aggregation;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;

import java.util.Map;

public class AggregationProgram extends FlinkDataProcessorRuntime<AggregationParameters> {

  public AggregationProgram(AggregationParameters params) {
    super(params);
    setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
  }

  public AggregationProgram(AggregationParameters params, FlinkDeploymentConfig config) {
    super(params, config);
    setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
  }

  @Override
  protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>... dataStreams) {
    return getKeyedStream(dataStreams[0]);
  }

  private DataStream<Map<String, Object>> getKeyedStream(DataStream<Map<String, Object>> dataStream) {
    if (params.getGroupBy().size() > 0) {
      return dataStream
              .keyBy(getKeySelector())
              .window(SlidingEventTimeWindows.of(Time.seconds(params.getTimeWindowSize()), Time.seconds(params.getOutputEvery())))
              .apply(new Aggregation(params.getAggregationType(), params.getAggregate(), params.getGroupBy().get(0)));
    } else {
      return dataStream.timeWindowAll(Time.seconds(params.getTimeWindowSize()), Time.seconds(params.getOutputEvery()))
              .apply(new Aggregation(params.getAggregationType(), params.getAggregate()));
    }
  }

  private KeySelector<Map<String, Object>, String> getKeySelector() {
    // TODO allow multiple keys
    String groupBy = params.getGroupBy().get(0);
    return new KeySelector<Map<String, Object>, String>() {
      @Override
      public String getKey(Map<String, Object> in) throws Exception {
        return String.valueOf(in.get(groupBy));
      }
    };
  }
}
