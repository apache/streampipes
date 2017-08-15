package org.streampipes.pe.mixed.flink.samples.statistics;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by riemer on 29.01.2017.
 */
public class StatisticsSummaryCalculator implements FlatMapFunction<Map<String, Object>, Map<String, Object>> {

  private String listPropertyName;

  public StatisticsSummaryCalculator(String listPropertyName) {
    this.listPropertyName = listPropertyName;
  }

  @Override
  public void flatMap(Map<String, Object> in, Collector<Map<String, Object>> out) throws
          Exception {
    List<Double> listValues = ((List<Object>) in
            .get(listPropertyName))
            .stream()
            .map(o -> Double.parseDouble(o.toString()))
            .collect(Collectors.toList());

    SummaryStatistics stats = new SummaryStatistics();

    listValues.forEach(lv -> stats.addValue(lv));

    in.put(StatisticsSummaryController.MIN, stats.getMin());
    in.put(StatisticsSummaryController.MAX, stats.getMax());
    in.put(StatisticsSummaryController.MEAN, stats.getMean());
    in.put(StatisticsSummaryController.N, stats.getN());
    in.put(StatisticsSummaryController.SUM, stats.getSum());
    in.put(StatisticsSummaryController.STDDEV, stats.getStandardDeviation());
    in.put(StatisticsSummaryController.VARIANCE, stats.getVariance());

    out.collect(in);

  }
}
