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

package org.apache.streampipes.connect.shared.preprocessing.transform.stream;

import org.apache.streampipes.connect.shared.preprocessing.transform.TransformationRule;

import java.util.Map;

public class EventRateTransformationRule implements TransformationRule {

  private final long aggregationTimeWindow;

  //none (Values from last event), max, min, mean, sum (of the values in the time window)
  private final String aggregationType;

  private long lastSentToPipelineTimestamp = System.currentTimeMillis();

  public EventRateTransformationRule(long aggregationTimeWindow, String aggregationType) {
    this.aggregationTimeWindow = aggregationTimeWindow;
    this.aggregationType = aggregationType;
  }

  @Override
  public Map<String, Object> apply(Map<String, Object> event) {
    if (System.currentTimeMillis() > lastSentToPipelineTimestamp + aggregationTimeWindow) {
      switch (aggregationType) {
        case "none":
          lastSentToPipelineTimestamp = System.currentTimeMillis();
          return event;
//                case "max":
//                case "min":
//                case "mean":
//                case "sum":

      }
    }
    return null;
  }


}
