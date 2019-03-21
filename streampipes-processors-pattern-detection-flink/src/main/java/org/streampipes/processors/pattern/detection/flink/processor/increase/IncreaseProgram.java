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

package org.streampipes.processors.pattern.detection.flink.processor.increase;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.streampipes.model.runtime.Event;
import org.streampipes.processors.pattern.detection.flink.AbstractPatternDetectionProgram;
import org.streampipes.processors.pattern.detection.flink.processor.common.TimestampExtractor;

public class IncreaseProgram extends AbstractPatternDetectionProgram<IncreaseParameters> {

  public IncreaseProgram(IncreaseParameters params, boolean debug) {
    super(params, debug);
  }

  @Override
  public DataStream<Event> getApplicationLogic(DataStream<Event>... dataStreams) {
    String timestampField = params.getTimestampField();
    return dataStreams[0]
            .assignTimestampsAndWatermarks(new TimestampExtractor(timestampField))
            .keyBy(getKeySelector())
            .window(TumblingEventTimeWindows.of(Time.seconds(params.getDuration())))
            .apply(new Increase(params.getIncrease(), params.getOperation(), params.getMapping(), params
                    .getOutputProperties(), params.getGroupBy())).setParallelism(1);
  }

  private KeySelector<Event, String> getKeySelector() {
    String groupBy = params.getGroupBy();
    return new KeySelector<Event, String>() {
      @Override
      public String getKey(Event in) throws Exception {
        return in.getFieldBySelector(groupBy).getAsPrimitive().getAsString();
      }
    };
  }
}
