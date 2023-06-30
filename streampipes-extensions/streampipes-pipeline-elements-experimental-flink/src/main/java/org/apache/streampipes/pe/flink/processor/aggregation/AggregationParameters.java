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

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;

import java.util.List;

public class AggregationParameters extends ProcessorParams {

  // timeWindow (true) or countWindow (false)
  private boolean timeWindow;
  private AggregationType aggregationType;
  private int outputEvery;
  private int windowSize;
  private List<String> aggregateKeyList;
  private List<String> groupBy;
  private List<String> selectProperties;

  public AggregationParameters(
      DataProcessorInvocation graph,
      AggregationType aggregationType,
      int outputEvery,
      List<String> groupBy,
      List<String> aggregateKeyList,
      int windowSize,
      List<String> selectProperties,
      boolean timeWindow) {
    super(graph);
    this.aggregationType = aggregationType;
    this.outputEvery = outputEvery;
    this.groupBy = groupBy;
    this.windowSize = windowSize;
    this.aggregateKeyList = aggregateKeyList;
    this.selectProperties = selectProperties;
    this.timeWindow = timeWindow;
  }

  public AggregationType getAggregationType() {
    return aggregationType;
  }

  public int getOutputEvery() {
    return outputEvery;
  }

  public List<String> getGroupBy() {
    return groupBy;
  }

  public int getWindowSize() {
    return windowSize;
  }

  public List<String> getAggregateKeyList() {
    return aggregateKeyList;
  }

  public List<String> getSelectProperties() {
    return selectProperties;
  }

  public boolean getTimeWindow() {
    return timeWindow;
  }
}
