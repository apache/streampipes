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
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.streampipes.model.runtime.Event;
import org.streampipes.processors.aggregation.flink.AbstractAggregationProgram;

public class AggregationProgram extends AbstractAggregationProgram<AggregationParameters> {

  public AggregationProgram(AggregationParameters params, boolean debug) {
    super(params, debug);
    setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
  }

  @Override
  protected DataStream<Event> getApplicationLogic(DataStream<Event>... dataStreams) {
    return getKeyedStream(dataStreams[0]);
  }

  private DataStream<Event> getKeyedStream(DataStream<Event> dataStream) {
    boolean timeWindow = true;
    long numberOfElements = 10;
    long every = 5;
    if (bindingParams.getGroupBy().size() > 0) {
      KeyedStream<Event, String> keyedStream = dataStream.keyBy(getKeySelector());
      if (timeWindow) {
        return keyedStream
                .window(SlidingEventTimeWindows.of(Time.seconds(bindingParams.getTimeWindowSize()), Time.seconds(bindingParams.getOutputEvery())))
                .apply(new TimeAggregation(bindingParams.getAggregationType(), bindingParams.getAggregate(), bindingParams.getGroupBy().get(0)));
      } else {
        // TODO add a possibility to send at least every X seconds a new event (using a trigger)
        return keyedStream
                .countWindow(numberOfElements, every)
                .apply(new CountAggregation(bindingParams.getAggregationType(), bindingParams.getAggregate(), bindingParams.getGroupBy().get(0)));
      }
    } else {
      if (timeWindow) {
        return dataStream
                .timeWindowAll(Time.seconds(bindingParams.getTimeWindowSize()), Time.seconds(bindingParams.getOutputEvery()))
                .apply(new TimeAggregation(bindingParams.getAggregationType(), bindingParams.getAggregate()));
      } else {
        return dataStream
                .countWindowAll(numberOfElements, every)
                .apply(new CountAggregation(bindingParams.getAggregationType(), bindingParams.getAggregate()));
      }
    }
  }

  private KeySelector<Event, String> getKeySelector() {
    // TODO allow multiple keys
    String groupBy = bindingParams.getGroupBy().get(0);
    return (in -> String.valueOf(in.getFieldBySelector(groupBy)));
  }
}
