/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.processors.aggregation.flink.processor.aggregation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregationTestData {

  private List<Map<String, Object>> expectedOutput;
  private List<Map<String, Object>> input;

  public AggregationTestData() {
    buildOutput();
    buildInput();
  }

  private void buildOutput() {
    this.expectedOutput = new ArrayList<>();
    this.expectedOutput.add(buildOutputMap(1, 1.0));
    this.expectedOutput.add(buildOutputMap(2, 1.5));
  }

  private void buildInput() {
    this.input = new ArrayList<>();
    input.add(buildMap(1));
    input.add(buildMap(2));
  }

  private Map<String, Object> buildOutputMap(Object value, Object aggregatedValue) {
    Map<String, Object> map = buildMap(value);
    map.put("aggregatedValue", aggregatedValue);
    return map;
  }

  private Map<String, Object> buildMap(Object value) {
    Map<String, Object> map = new HashMap<>();
    map.put("sensorId", "a");
    map.put("value", value);
    return map;
  }

  public List<Map<String, Object>> getExpectedOutput() {
    return expectedOutput;
  }

  public List<Map<String, Object>> getInput() {
    return input;
  }
}
