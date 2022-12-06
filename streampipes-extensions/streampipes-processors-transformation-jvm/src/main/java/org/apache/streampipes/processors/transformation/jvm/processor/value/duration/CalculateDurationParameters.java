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

package org.apache.streampipes.processors.transformation.jvm.processor.value.duration;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class CalculateDurationParameters extends EventProcessorBindingParams {
  private String startTs;
  private String endTs;
  private String unit;
  private String durationName;

  public CalculateDurationParameters(DataProcessorInvocation graph,
                                     String startTs,
                                     String endTs,
                                     String unit,
                                     String durationName) {
    super(graph);
    this.startTs = startTs;
    this.endTs = endTs;
    this.unit = unit;
    this.durationName = durationName;
  }

  public String getStartTs() {
    return startTs;
  }

  public String getEndTs() {
    return endTs;
  }

  public String getUnit() {
    return unit;
  }

  public String getDurationName() {
    return durationName;
  }
}