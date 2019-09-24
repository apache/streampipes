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

package org.streampipes.processors.pattern.detection.flink.processor.and;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

import java.util.List;

public class AndParameters extends EventProcessorBindingParams {

  private TimeUnit timeUnit;
  private Integer timeWindow;

  private List<String> leftMappings;
  private List<String> rightMappings;


  public AndParameters(DataProcessorInvocation invocationGraph, TimeUnit timeUnit, Integer timeWindow, List<String> leftMappings, List<String> rightMappings) {
    super(invocationGraph);
    this.timeUnit = timeUnit;
    this.timeWindow = timeWindow;
    this.leftMappings = leftMappings;
    this.rightMappings = rightMappings;
  }

  public TimeUnit getTimeUnit() {
    return timeUnit;
  }

  public Integer getTimeWindow() {
    return timeWindow;
  }

  public List<String> getLeftMappings() {
    return leftMappings;
  }

  public List<String> getRightMappings() {
    return rightMappings;
  }
}
