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

package org.apache.streampipes.processors.statistics.flink.processor.stat.window;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class StatisticsSummaryParamsSerializable implements Serializable {

  private String valueToObserve;
  private String timestampMapping;
  private String groupBy;
  private long timeWindowSize;
  private TimeUnit timeUnit;

  public StatisticsSummaryParamsSerializable(String valueToObserve, String timestampMapping,
                                             String groupBy, Long timeWindowSize, TimeUnit
                                                 timeUnit) {
    this.valueToObserve = valueToObserve;
    this.timestampMapping = timestampMapping;
    this.groupBy = groupBy;
    this.timeWindowSize = timeWindowSize;
    this.timeUnit = timeUnit;
  }


  public String getValueToObserve() {
    return valueToObserve;
  }

  public String getGroupBy() {
    return groupBy;
  }

  public Long getTimeWindowSize() {
    return timeWindowSize;
  }

  public TimeUnit getTimeUnit() {
    return timeUnit;
  }

  public String getTimestampMapping() {
    return timestampMapping;
  }
}
