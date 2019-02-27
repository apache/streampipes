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
import org.streampipes.model.runtime.Event;

import java.util.ArrayList;
import java.util.List;

public class Increase implements WindowFunction<Event, Event, String, TimeWindow> {

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
  public void apply(String key, TimeWindow window, Iterable<Event> input, Collector<Event>
          out) throws Exception {

    List<Double> values = new ArrayList<>();
    Event lastEvent = new Event();

    for (Event anInput : input) {
      lastEvent = anInput;
      if (lastEvent.getFieldBySelector(groupByFieldName).getAsPrimitive().getAsString().equals(key)) {
        values.add(lastEvent.getFieldBySelector(propertyFieldName).getAsPrimitive().getAsDouble());
      }
    }
    if (values.size() > 0) {
      if (operation == Operation.INCREASE) {
        if (values.get(values.size()-1) >= values.get(0) * (1 + increaseValue / 100)) {
          buildOutput(out, lastEvent);
        }
      } else {
        // 1 <= 2 - (2* (1- 10 / 100) <=> 1 <= 2*0
        if (values.get(values.size()-1) <= values.get(0) - (values.get(values.size()-1) * (1 - (increaseValue / 100)))) {
          buildOutput(out, lastEvent);
        }
      }
    }
  }

  private void buildOutput(Collector<Event> out, Event lastEvent) {
    Event outEvent = new Event();
    for(String outputProperty : outputProperties) {
      outEvent.addField(lastEvent.getFieldBySelector(outputProperty));
    }

    out.collect(outEvent);
  }

}
