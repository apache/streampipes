package org.streampipes.pe.mixed.flink.samples.peak;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PeakDetectionCalculator implements FlatMapFunction<List<Map<String,
        Object>>, Map<String, Object>> {

  private String groupBy;
  private String valueToObserve;
  private Integer lag;
  private Double threshold;
  private Double influence;

  public PeakDetectionCalculator(String groupBy, String valueToObserve, Integer lag, Double
          threshold, Double influence) {
    this.groupBy = groupBy;
    this.valueToObserve = valueToObserve;
    this.lag = lag;
    this.threshold = threshold;
    this.influence = influence;
  }


  @Override
  public void flatMap(List<Map<String, Object>> in, Collector<Map<String, Object>> out)
          throws Exception {
    List<Double> y = in
            .stream()
            .map(m -> Double.parseDouble(String.valueOf(m.get(valueToObserve))))
            .collect(Collectors.toList());

    Integer[] signals = makeIntegerArray(y.size());
    Double[] filteredY = makeDoubleArray(y.size());

    if (in.size() >= (lag+1)) {
      for (int i = 0; i < lag; i++) {
        filteredY[i] = y.get(i);
      }

      Double[] avgFilter = makeDoubleArray(y.size());
      Double[] stdFilter = makeDoubleArray(y.size());

      avgFilter[lag] = mean(y.subList(0, lag));
      stdFilter[lag] = std(y.subList(0, lag));

      for (int i = (lag + 1); i < y.size(); i++) {
        Double f = y.get(i);

        if (Math.abs((y.get(i) - avgFilter[i - 1])) > threshold * stdFilter[i - 1]) {
          if (y.get(i) > avgFilter[i - 1]) {
            signals[i] = 1;
          } else {
            signals[i] = -1;
          }
          filteredY[i] = influence * f + (1 - influence) * filteredY[i - 1];
          avgFilter[i] = mean(Arrays.asList(Arrays.copyOfRange(filteredY, (i - lag), i)));
          stdFilter[i] = std(Arrays.asList(Arrays.copyOfRange(filteredY, (i - lag), i)));
        } else {
          signals[i] = 0;
          filteredY[i] = y.get(i);
          avgFilter[i] = mean(Arrays.asList(Arrays.copyOfRange(filteredY, (i - lag), i)));
          stdFilter[i] = std(Arrays.asList(Arrays.copyOfRange(filteredY, (i - lag), i)));
        }
      }

      Map<String, Object> outMap = new HashMap<>();
      outMap.put("id", in.get(in.size() - 1).get(groupBy));
      outMap.put("timestamp", System.currentTimeMillis());
      outMap.put("signal", signals[signals.length - 1]);

      System.out.println("signal=" + signals[signals.length - 1]);
      out.collect(outMap);
    }

  }

  private Double[] makeDoubleArray(int size) {
    Double[] array = new Double[size];
    for (int i = 0; i < array.length; i++) {
      array[i] = 0.0;
    }
    return array;
  }

  private Integer[] makeIntegerArray(int size) {
    Integer[] array = new Integer[size];
    for (int i = 0; i < array.length; i++) {
      array[i] = 0;
    }
    return array;
  }

  private Double mean(List<Double> values) {
    SummaryStatistics stats = new SummaryStatistics();

    values.forEach(lv -> stats.addValue(lv));

    return stats.getMean();
  }

  private Double std(List<Double> values) {
    SummaryStatistics stats = new SummaryStatistics();

    values.forEach(lv -> stats.addValue(lv));

    return stats.getStandardDeviation();
  }
}
