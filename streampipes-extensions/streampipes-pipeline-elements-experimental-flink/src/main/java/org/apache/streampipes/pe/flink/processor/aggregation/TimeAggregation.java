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

import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.List;
import java.util.Map;

public class TimeAggregation extends Aggregation
    implements WindowFunction<Event, Event, Map<String, String>, TimeWindow>,
    AllWindowFunction<Event, Event, TimeWindow> {

  // Keyed stream
  public TimeAggregation(AggregationType aggregationType, List<String> fieldsToAggregate, List<String> keyIdentifiers) {
    super(aggregationType, fieldsToAggregate, keyIdentifiers);
  }

  // Not keyed stream
  public TimeAggregation(AggregationType aggregationType, List<String> fieldsToAggregate) {
    super(aggregationType, fieldsToAggregate);
  }

  @Override
  public void apply(Map<String, String> keys, TimeWindow window, Iterable<Event> input, Collector<Event> out) {
    process(input, out);
  }

  @Override
  public void apply(TimeWindow window, Iterable<Event> input, Collector<Event> out) throws Exception {
    process(input, out);
  }
}
