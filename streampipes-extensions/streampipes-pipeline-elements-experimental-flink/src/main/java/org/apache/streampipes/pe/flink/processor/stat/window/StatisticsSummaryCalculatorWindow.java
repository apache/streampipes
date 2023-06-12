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
import org.apache.streampipes.pe.flink.processor.stat.summary.StatisticsSummaryController;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticsSummaryCalculatorWindow implements FlatMapFunction<List<Event>, Event>,
    Serializable {

  private String partitionMapping;
  private String valueToObserveMapping;

  public StatisticsSummaryCalculatorWindow(String partitionMapping, String valueToObserveMapping) {
    this.partitionMapping = partitionMapping;
    this.valueToObserveMapping = valueToObserveMapping;
  }

  @Override
  public void flatMap(List<Event> in, Collector<Event> out)
      throws Exception {
    List<Double> listValues = (in.stream().map(m -> m.getFieldBySelector(valueToObserveMapping)
            .getAsPrimitive().getAsDouble())
        .collect(Collectors.toList()));

    SummaryStatistics stats = new SummaryStatistics();

    listValues.forEach(lv -> stats.addValue(lv));

    Event outMap = new Event();

    outMap.addField("timestamp", System.currentTimeMillis());
    outMap.addField("id", in.get(in.size() - 1).getFieldBySelector(partitionMapping).getRawValue());
    outMap.addField(StatisticsSummaryController.MIN, stats.getMin());
    outMap.addField(StatisticsSummaryController.MAX, stats.getMax());
    outMap.addField(StatisticsSummaryController.MEAN, stats.getMean());
    outMap.addField(StatisticsSummaryController.N, stats.getN());
    outMap.addField(StatisticsSummaryController.SUM, stats.getSum());
    outMap.addField(StatisticsSummaryController.STDDEV, stats.getStandardDeviation());
    outMap.addField(StatisticsSummaryController.VARIANCE, stats.getVariance());

    out.collect(outMap);
  }

}
