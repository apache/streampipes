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

package org.apache.streampipes.processors.geo.jvm.jts.processor.trajectory;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class CreateTrajectoryFromPointsParameter extends EventProcessorBindingParams {

  private String epsg;
  private String wkt;
  private String description;
  private Integer subpoints;
  private String m;

  public CreateTrajectoryFromPointsParameter(DataProcessorInvocation graph, String wkt, String epsg, String description, Integer subpoints, String m) {
    super(graph);
    this.wkt = wkt;
    this.epsg = epsg;
    this.description = description;
    this.subpoints = subpoints;
    this.m = m;
  }

  public String getEpsg() {
    return epsg;
  }

  public String getWkt() {
    return wkt;
  }

  public String getDescription() {
    return description;
  }

  public Integer getSubpoints() {
    return subpoints;
  }

  public String getM() {
    return m;
  }
}
