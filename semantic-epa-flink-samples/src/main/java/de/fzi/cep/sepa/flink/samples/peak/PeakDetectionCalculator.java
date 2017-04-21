package de.fzi.cep.sepa.flink.samples.peak;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by riemer on 20.04.2017.
 */
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
    List<Double> values = in
            .stream()
            .map(m -> Double.parseDouble(String.valueOf(m.get(valueToObserve))))
            .collect(Collectors.toList());


  }
}
