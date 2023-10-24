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

package org.apache.streampipes.model.connect.rules.stream;

import org.apache.streampipes.model.connect.rules.ITransformationRuleVisitor;
import org.apache.streampipes.model.connect.rules.TransformationRulePriority;

public class EventRateTransformationRuleDescription extends StreamTransformationRuleDescription {

  private long aggregationTimeWindow;

  //None (Values from last event), max, min, mean, sum (of the values in the time window)
  private String aggregationType;


  public EventRateTransformationRuleDescription() {

  }

  public EventRateTransformationRuleDescription(EventRateTransformationRuleDescription other) {
    super(other);
    this.aggregationTimeWindow = other.getAggregationTimeWindow();
    this.aggregationType = other.getAggregationType();
  }

  public long getAggregationTimeWindow() {
    return aggregationTimeWindow;
  }

  public void setAggregationTimeWindow(long aggregationTimeWindow) {
    this.aggregationTimeWindow = aggregationTimeWindow;
  }

  public String getAggregationType() {
    return aggregationType;
  }

  public void setAggregationType(String aggregationTypes) {
    this.aggregationType = aggregationTypes;
  }

  @Override
  public void accept(ITransformationRuleVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public int getRulePriority() {
    return TransformationRulePriority.EVENT_RATE.getCode();
  }
}
