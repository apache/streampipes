/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.processors.aggregation.flink.processor.count;

import org.apache.flink.streaming.api.windowing.time.Time;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class CountParameters extends EventProcessorBindingParams {

  private Time time;
  private String fieldToCount;

  public CountParameters(DataProcessorInvocation graph, Time time, String fieldToCount) {
    super(graph);
    this.time = time;
    this.fieldToCount = fieldToCount;
  }

  public Time getTime() {
    return time;
  }

  public String getFieldToCount() {
    return fieldToCount;
  }
}