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
package org.streampipes.processors.aggregation.flink.processor.rate;

import org.apache.flink.shaded.guava18.com.google.common.collect.Iterables;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.streampipes.model.runtime.Event;

public class EventRate implements AllWindowFunction<Event, Float, TimeWindow>{

  private Integer timeWindowSize;

  public EventRate(Integer timeWindowSize) {
    this.timeWindowSize = timeWindowSize;
  }

  @Override
  public void apply(TimeWindow timeWindow, Iterable<Event> iterable, Collector<Float> collector)
          throws Exception {
    collector.collect((float) Iterables.size(iterable) / timeWindowSize);
  }
}
