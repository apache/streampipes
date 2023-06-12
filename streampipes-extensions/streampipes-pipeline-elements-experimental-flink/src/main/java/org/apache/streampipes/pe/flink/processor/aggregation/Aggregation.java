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

package org.apache.streampipes.pe.flink.processor.aggregation;

import org.apache.streampipes.model.runtime.Event;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Aggregation implements Serializable {

  private AggregationType aggregationType;
  //  private String fieldToAggregate;
  private List<String> fieldsToAggregate;
  private List<String> keyIdentifiers;

  // Used for keyed streams
  public Aggregation(AggregationType aggregationType, List<String> fieldsToAggregate, List<String> keyIdentifiers) {
    this.aggregationType = aggregationType;
    this.fieldsToAggregate = fieldsToAggregate;
    this.keyIdentifiers = keyIdentifiers;
  }

  // Used for not keyed streams
  public Aggregation(AggregationType aggregationType, List<String> fieldsToAggregate) {
    this.aggregationType = aggregationType;
    this.fieldsToAggregate = fieldsToAggregate;
    this.keyIdentifiers = null;
  }


  protected Double getAggregate(List<Double> values) {
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

  // Gets called every time a new event is fired, i.e. when an aggregation has to be calculated
  protected void process(Iterable<Event> input, Collector<Event> out) {
    List<Double> values = new ArrayList<>();
    Event lastEvent = new Event();

    // Adds the values of all recent events in input to aggregate them later
    // Dumps thereby all previous events and only emits the most recent event in the window with the
    // aggregated value added
    for (Event anInput : input) {
      for (String aggregate : fieldsToAggregate) {
        values.add(anInput.getFieldBySelector(aggregate).getAsPrimitive().getAsDouble());
        lastEvent = anInput;

        String propertyPrefix = StringUtils.substringAfterLast(aggregate, ":");
        String runtimeName = propertyPrefix + "_" + aggregationType.toString().toLowerCase();

        lastEvent.addField(runtimeName, getAggregate(values));
      }
    }

    out.collect(lastEvent);
  }
}
