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

package org.streampipes.processors.pattern.detection.flink.processor.increase;

import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Increase implements WindowFunction<Map<String, Object>, Map<String, Object>, String, TimeWindow> {

  private String propertyFieldName;
  private Integer increaseValue;
  private Operation operation;
  private List<String> outputProperties;
  private String groupByFieldName;

  public Increase(Integer increaseValue, Operation operation, String mapping, List<String> outputProperties, String
          groupByFieldName) {
    this.propertyFieldName = mapping;
    this.increaseValue = increaseValue;
    this.operation = operation;
    this.outputProperties = outputProperties;
    this.groupByFieldName = groupByFieldName;
  }

  @Override
  public void apply(String key, TimeWindow window, Iterable<Map<String, Object>> input, Collector<Map<String, Object>>
          out) throws Exception {

    List<Double> values = new ArrayList<>();
    Map<String, Object> lastEvent = new HashMap<>();

    for (Map<String, Object> anInput : input) {
      lastEvent = anInput;
      if (String.valueOf(lastEvent.get(groupByFieldName)).equals(key)) {
        values.add(Double.parseDouble(String.valueOf(lastEvent.get(propertyFieldName))));
      }
    }
    if (values.size() > 0) {
      if (operation == Operation.INCREASE) {
        if (values.get(values.size()-1) > values.get(0) * (1 + increaseValue / 100)) {
          buildOutput(out, lastEvent);
        }
      } else {
        if (values.get(values.size()-1) > values.get(0) * (1 - increaseValue / 100)) {
          buildOutput(out, lastEvent);
        }
      }
    }
  }

  private void buildOutput(Collector<Map<String, Object>> out, Map<String, Object> lastEvent) {
    Map<String, Object> outEvent = new HashMap<>();
    for(String outputProperty : outputProperties) {
      outEvent.put(outputProperty, lastEvent.get(outputProperty));
    }

    out.collect(outEvent);
  }

}
