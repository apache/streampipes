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

package org.streampipes.processors.statistics.flink.processor.stat;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.streampipes.model.runtime.Event;

import java.util.List;

public class StatisticsSummaryCalculator implements FlatMapFunction<Event, Event> {

  private String listPropertyName;

  public StatisticsSummaryCalculator(String listPropertyName) {
    this.listPropertyName = listPropertyName;
  }

  @Override
  public void flatMap(Event in, Collector<Event> out) throws
          Exception {
    List<Double> listValues = (in.getFieldBySelector(listPropertyName).getAsList().castItems
            (Double.class));

    SummaryStatistics stats = new SummaryStatistics();

    listValues.forEach(stats::addValue);

    in.addField(StatisticsSummaryController.MIN, stats.getMin());
    in.addField(StatisticsSummaryController.MAX, stats.getMax());
    in.addField(StatisticsSummaryController.MEAN, stats.getMean());
    in.addField(StatisticsSummaryController.N, stats.getN());
    in.addField(StatisticsSummaryController.SUM, stats.getSum());
    in.addField(StatisticsSummaryController.STDDEV, stats.getStandardDeviation());
    in.addField(StatisticsSummaryController.VARIANCE, stats.getVariance());

    out.collect(in);

  }
}
