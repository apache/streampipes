/*
Copyright 2019 FZI Forschungszentrum Informatik

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
package org.streampipes.wrapper.flink.converter;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.streampipes.model.runtime.Event;
import org.streampipes.wrapper.params.runtime.RuntimeParams;

import java.util.Map;

public class EventGenerator<RP extends RuntimeParams<?, ?>> implements FlatMapFunction<Map<String,
        Object>, Event> {

  private static final long serialVersionUID = 1L;

  private RP runtimeParams;
  private String sourceId;

  public EventGenerator(String sourceId, RP runtimeParams) {
    this.sourceId = sourceId;
    this.runtimeParams = runtimeParams;
  }

  @Override
  public void flatMap(Map<String, Object> inMap, Collector<Event> collector) throws Exception {
    collector.collect(runtimeParams.makeEvent(inMap, sourceId));
  }
}
