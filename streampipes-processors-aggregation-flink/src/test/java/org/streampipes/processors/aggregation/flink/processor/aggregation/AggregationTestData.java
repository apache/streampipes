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

import org.streampipes.model.runtime.Event;

import java.util.ArrayList;
import java.util.List;

public class AggregationTestData {

  private List<Event> expectedOutput;
  private List<Event> input;

  public AggregationTestData() {
    buildOutput();
    buildInput();
  }

  private void buildOutput() {
    this.expectedOutput = new ArrayList<>();
    this.expectedOutput.add(buildOutputMap(1.0f, 1.0f));
    this.expectedOutput.add(buildOutputMap(2.0f, 1.5f));
  }

  private void buildInput() {
    this.input = new ArrayList<>();
    input.add(buildEvent(1.0f));
    input.add(buildEvent(2.0f));
  }

  private Event buildOutputMap(Float value, Float aggregatedValue) {
    Event event = buildEvent(value);
    event.addField("aggregatedValue", aggregatedValue);
    return event;
  }

  private Event buildEvent(Float value) {
   Event event = new Event();
    event.addField("sensorId", "a");
    event.addField("value", value);
    return event;
  }

  public List<Event> getExpectedOutput() {
    return expectedOutput;
  }

  public List<Event> getInput() {
    return input;
  }
}
