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

package org.apache.streampipes.pe.flink.processor.absence;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.pe.flink.processor.and.TimeUnit;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;

import java.util.ArrayList;
import java.util.List;

public class AbsenceParameters extends ProcessorParams {

  private static final long serialVersionUID = 4319341875274736697L;

  private List<String> selectProperties = new ArrayList<>();
  private Integer timeWindowSize;
  private TimeUnit timeUnit;

  public AbsenceParameters(DataProcessorInvocation graph, List<String> selectProperties, Integer timeWindowSize,
                           TimeUnit timeUnit) {
    super(graph);
    this.selectProperties = selectProperties;
    this.timeWindowSize = timeWindowSize;
    this.timeUnit = timeUnit;
  }

  public List<String> getSelectProperties() {
    return selectProperties;
  }

  public Integer getTimeWindowSize() {
    return timeWindowSize;
  }

  public TimeUnit getTimeUnit() {
    return timeUnit;
  }
}
