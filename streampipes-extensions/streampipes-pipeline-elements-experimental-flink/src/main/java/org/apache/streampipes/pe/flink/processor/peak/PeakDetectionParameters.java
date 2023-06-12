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

package org.apache.streampipes.pe.flink.processor.peak;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;

import java.io.Serializable;

/**
 * Created by riemer on 20.04.2017.
 */
public class PeakDetectionParameters extends ProcessorParams implements Serializable {

  private String valueToObserve;
  private String timestampMapping;
  private String groupBy;
  private Integer lag;
  private Double threshold;
  private Double influence;
  private Integer countWindowSize;

  public PeakDetectionParameters(DataProcessorInvocation graph) {
    super(graph);
  }

  public PeakDetectionParameters(DataProcessorInvocation graph, String valueToObserve, String
      timestampMapping, String groupBy, Integer countWindowSize, Integer lag, Double
                                     threshold, Double
                                     influence) {
    super(graph);
    this.valueToObserve = valueToObserve;
    this.timestampMapping = timestampMapping;
    this.groupBy = groupBy;
    this.lag = lag;
    this.threshold = threshold;
    this.influence = influence;
    this.countWindowSize = countWindowSize;
  }

  public String getValueToObserve() {
    return valueToObserve;
  }

  public String getTimestampMapping() {
    return timestampMapping;
  }

  public String getGroupBy() {
    return groupBy;
  }

  public Integer getLag() {
    return lag;
  }

  public Double getThreshold() {
    return threshold;
  }

  public Double getInfluence() {
    return influence;
  }

  public Integer getCountWindowSize() {
    return countWindowSize;
  }
}
