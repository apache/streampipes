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
package org.apache.streampipes.processors.filters.jvm.processor.merge;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;

import java.util.List;

public class MergeByTimeParameters extends EventProcessorBindingParams {

  private List<String> outputKeySelectors;
  private String timestampFieldStream1;
  private String timestampFieldStream2;
  private Integer timeInterval;

  public MergeByTimeParameters(DataProcessorInvocation graph, List<String> outputKeySelectors,
                               String timestampFieldStream1, String timestampFieldStream2, Integer timeInterval) {
    super(graph);
    this.outputKeySelectors = outputKeySelectors;
    this.timestampFieldStream1 = timestampFieldStream1;
    this.timestampFieldStream2 = timestampFieldStream2;
    this.timeInterval = timeInterval;
  }

  public List<String> getOutputKeySelectors() {
    return outputKeySelectors;
  }

  public String getTimestampFieldStream1() {
    return timestampFieldStream1;
  }

  public String getTimestampFieldStream2() {
    return timestampFieldStream2;
  }

  public Integer getTimeInterval() {
    return timeInterval;
  }
}
