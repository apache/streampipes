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

package org.apache.streampipes.processors.geo.jvm.processor.distancecalculator;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class DistanceCalculatorParameters extends EventProcessorBindingParams {

  public String lat1PropertyName;
  public String long1PropertyName;
  public String lat2PropertyName;
  public String long2PropertyName;

  public DistanceCalculatorParameters(DataProcessorInvocation graph) {
    super(graph);
  }

  public DistanceCalculatorParameters(DataProcessorInvocation graph, String lat1PropertyName, String long1PropertyName,
                                      String lat2PropertyName, String long2PropertyName) {
    super(graph);
    this.lat1PropertyName = lat1PropertyName;
    this.long1PropertyName = long1PropertyName;
    this.lat2PropertyName = lat2PropertyName;
    this.long2PropertyName = long2PropertyName;
  }


  public String getLat1PropertyName() {
    return lat1PropertyName;
  }

  public String getLong1PropertyName() {
    return long1PropertyName;
  }

  public String getLat2PropertyName() {
    return lat2PropertyName;
  }

  public String getLong2PropertyName() {
    return long2PropertyName;
  }
}
