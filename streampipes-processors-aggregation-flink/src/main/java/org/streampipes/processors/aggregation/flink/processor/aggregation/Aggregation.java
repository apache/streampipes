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

package org.streampipes.processors.aggregation.flink.processor.aggregation;

import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.*;

public class Aggregation implements WindowFunction<Map<String, Object>, Map<String, Object>, String, TimeWindow>,
        AllWindowFunction<Map<String, Object>, Map<String, Object>, TimeWindow> {

  private AggregationType aggregationType;
  private String fieldToAggregate;
  private String keyIdentifier;
  private Boolean keyedStream = false;

  public Aggregation(AggregationType aggregationType, String fieldToAggregate, String keyIdentifier) {
    this(aggregationType, fieldToAggregate);
    this.keyedStream = true;
    this.keyIdentifier = keyIdentifier;
  }

  public Aggregation(AggregationType aggregationType, String fieldToAggregate) {
    this.aggregationType = aggregationType;
    this.fieldToAggregate = fieldToAggregate;
  }

  @Override
  public void apply(String key, TimeWindow window, Iterable<Map<String, Object>> input, Collector<Map<String, Object>>
          out) throws Exception {
    process(input, out, key);
  }

  private Double getAggregate(List<Double> values) {
    if (aggregationType == AggregationType.AVG) {
      return values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    } else if (aggregationType == AggregationType.MAX) {
      return Collections.max(values);
    } else if (aggregationType == AggregationType.MIN) {
      return Collections.min(values);
    } else {
      return values.stream().mapToDouble(Double::doubleValue).sum();
    }

  }

  @Override
  public void apply(TimeWindow window, Iterable<Map<String, Object>> input, Collector<Map<String, Object>> out) throws
          Exception {
    process(input, out, null);
  }

  private void process(Iterable<Map<String, Object>> input, Collector<Map<String, Object>> out, String key) {
    List<Double> values = new ArrayList<>();
    Map<String, Object> lastEvent = new HashMap<>();

    for (Map<String, Object> anInput : input) {
      lastEvent = anInput;
      if (!keyedStream || String.valueOf(lastEvent.get(keyIdentifier)).equals(key)) {
        values.add(Double.parseDouble(String.valueOf(lastEvent.get(fieldToAggregate))));
      }
    }

    lastEvent.put("aggregatedValue", getAggregate(values));
    out.collect(lastEvent);
  }
}
