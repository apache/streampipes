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

package org.apache.streampipes.pe.flink.processor.stat.summary;

import org.apache.streampipes.model.runtime.Event;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.List;

public class StatisticsSummaryCalculator implements FlatMapFunction<Event, Event> {

  private List<String> listPropertyMappings;

  public StatisticsSummaryCalculator(List<String> listPropertyMappings) {
    this.listPropertyMappings = listPropertyMappings;
  }

  @Override
  public void flatMap(Event in, Collector<Event> out) throws
      Exception {

    for (String property : listPropertyMappings) {
      List<Double> listValues = (in.getFieldBySelector(property).getAsList().castItems
          (Double.class));

      SummaryStatistics stats = new SummaryStatistics();

      listValues.forEach(stats::addValue);

      String propertyPrefix = StringUtils.substringAfterLast(property, ":");
      in.addField(propertyPrefix + "_" + StatisticsSummaryController.MIN, stats.getMin());
      in.addField(propertyPrefix + "_" + StatisticsSummaryController.MAX, stats.getMax());
      in.addField(propertyPrefix + "_" + StatisticsSummaryController.MEAN, stats.getMean());
      in.addField(propertyPrefix + "_" + StatisticsSummaryController.N, stats.getN());
      in.addField(propertyPrefix + "_" + StatisticsSummaryController.SUM, stats.getSum());
      in.addField(propertyPrefix + "_" + StatisticsSummaryController.STDDEV, stats.getStandardDeviation());
      in.addField(propertyPrefix + "_" + StatisticsSummaryController.VARIANCE, stats.getVariance());

    }

    out.collect(in);

  }
}
