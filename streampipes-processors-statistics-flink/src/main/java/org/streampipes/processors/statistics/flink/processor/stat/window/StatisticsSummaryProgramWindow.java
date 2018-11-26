/*
 * Copyright 2018 FZI Forschungszentrum Informatik
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
 *
 */

package org.streampipes.processors.statistics.flink.processor.stat.window;

import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.streampipes.processors.statistics.flink.AbstractStatisticsProgram;
import org.streampipes.processors.statistics.flink.extensions.MapKeySelector;
import org.streampipes.processors.statistics.flink.extensions.SlidingEventTimeWindow;
import org.streampipes.processors.statistics.flink.extensions.TimestampMappingFunction;

import java.util.List;
import java.util.Map;

public class StatisticsSummaryProgramWindow extends
        AbstractStatisticsProgram<StatisticsSummaryParametersWindow> {

  private StatisticsSummaryParamsSerializable serializableParams;

  public StatisticsSummaryProgramWindow(StatisticsSummaryParametersWindow params, StatisticsSummaryParamsSerializable serializableParams, boolean debug) {
    super(params, debug);
    this.streamTimeCharacteristic = TimeCharacteristic.EventTime;
    this.serializableParams = serializableParams;
  }

  public StatisticsSummaryProgramWindow(StatisticsSummaryParametersWindow params, StatisticsSummaryParamsSerializable serializableParams) {
    super(params);
    this.streamTimeCharacteristic = TimeCharacteristic.EventTime;
    this.serializableParams = serializableParams;
  }

  @Override
  protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>... messageStream) {

    StatisticsSummaryParamsSerializable sp = new
            StatisticsSummaryParamsSerializable(serializableParams.getValueToObserve(),
            serializableParams.getTimestampMapping(), serializableParams.getGroupBy(),
            serializableParams.getTimeWindowSize(), serializableParams.getTimeUnit());
    DataStream<Map<String, Object>> output = messageStream[0]
            .keyBy(new MapKeySelector(sp.getGroupBy()).getKeySelector())
            .transform
                    ("sliding-window-event-shift",
                            TypeInformation.of(new TypeHint<List<Map<String, Object>>>() {
                            }), new SlidingEventTimeWindow<>(sp.getTimeWindowSize(), sp.getTimeUnit(),
                                    (TimestampMappingFunction<Map<String, Object>>) in ->
                                            Long.parseLong(String.valueOf(in.get(sp.getTimestampMapping())))))
            .flatMap(new StatisticsSummaryCalculatorWindow(sp.getGroupBy(), sp.getValueToObserve()));

    return output;
  }




}