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

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.streampipes.pe.mixed.flink.samples.statistics.StatisticsSummaryController;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsSummaryCalculatorWindow implements FlatMapFunction<List<Map<String,
        Object>>, Map<String, Object>>, Serializable {

  private String partitionMapping;
  private String valueToObserveMapping;

  public StatisticsSummaryCalculatorWindow(String partitionMapping, String valueToObserveMapping) {
    this.partitionMapping = partitionMapping;
    this.valueToObserveMapping = valueToObserveMapping;
  }

  @Override
  public void flatMap(List<Map<String, Object>> in, Collector<Map<String, Object>> out)
          throws Exception {
    List<Double> listValues = (in.stream().map(m -> Double.parseDouble(String.valueOf(m.get
            (valueToObserveMapping))))
            .collect(Collectors.toList()));

    SummaryStatistics stats = new SummaryStatistics();

    listValues.forEach(lv -> stats.addValue(lv));

    Map<String, Object> outMap = new HashMap<>();

    outMap.put("timestamp", System.currentTimeMillis());
    outMap.put("id", in.get(in.size() - 1).get(partitionMapping));
    outMap.put(StatisticsSummaryController.MIN, stats.getMin());
    outMap.put(StatisticsSummaryController.MAX, stats.getMax());
    outMap.put(StatisticsSummaryController.MEAN, stats.getMean());
    outMap.put(StatisticsSummaryController.N, stats.getN());
    outMap.put(StatisticsSummaryController.SUM, stats.getSum());
    outMap.put(StatisticsSummaryController.STDDEV, stats.getStandardDeviation());
    outMap.put(StatisticsSummaryController.VARIANCE, stats.getVariance());

    out.collect(outMap);
  }

}
