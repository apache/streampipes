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

package org.apache.streampipes.pe.flink.processor.stat.window;

import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.pe.flink.extensions.MapKeySelector;
import org.apache.streampipes.pe.flink.extensions.SlidingEventTimeWindow;
import org.apache.streampipes.pe.flink.extensions.TimestampMappingFunction;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorProgram;

import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.List;

public class StatisticsSummaryProgramWindow extends
    FlinkDataProcessorProgram<StatisticsSummaryParametersWindow> {

  private StatisticsSummaryParamsSerializable serializableParams;

  public StatisticsSummaryProgramWindow(StatisticsSummaryParametersWindow params,
                                        StatisticsSummaryParamsSerializable serializableParams) {
    super(params);
    //this.streamTimeCharacteristic = TimeCharacteristic.EventTime;
    this.serializableParams = serializableParams;
  }

  @Override
  public DataStream<Event> getApplicationLogic(DataStream<Event>... messageStream) {

    StatisticsSummaryParamsSerializable sp = new
        StatisticsSummaryParamsSerializable(serializableParams.getValueToObserve(),
        serializableParams.getTimestampMapping(), serializableParams.getGroupBy(),
        serializableParams.getTimeWindowSize(), serializableParams.getTimeUnit());
    DataStream<Event> output = messageStream[0]
        .keyBy(new MapKeySelector(sp.getGroupBy()).getKeySelector())
        .transform
            ("sliding-window-event-shift",
                TypeInformation.of(new TypeHint<List<Event>>() {
                }), new SlidingEventTimeWindow<>(sp.getTimeWindowSize(), sp.getTimeUnit(),
                    (TimestampMappingFunction<Event>) in ->
                        in.getFieldBySelector(sp.getTimestampMapping())
                            .getAsPrimitive().getAsLong()))
        .flatMap(new StatisticsSummaryCalculatorWindow(sp.getGroupBy(), sp.getValueToObserve()));

    return output;
  }


}
