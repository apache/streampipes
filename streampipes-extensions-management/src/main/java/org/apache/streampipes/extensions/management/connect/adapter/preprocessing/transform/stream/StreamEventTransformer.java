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

package org.apache.streampipes.extensions.management.connect.adapter.preprocessing.transform.stream;

import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.transform.TransformationRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StreamEventTransformer implements StreamTransformationRule {

  private List<EventRateTransformationRule> eventRateTransformationRules;

  public StreamEventTransformer() {
    this.eventRateTransformationRules = new ArrayList<>();
  }


  public StreamEventTransformer(List<TransformationRule> rules) {
    this();

    for (TransformationRule rule : rules) {
      if (rule instanceof EventRateTransformationRule) {
        this.eventRateTransformationRules.add((EventRateTransformationRule) rule);
      }
    }
  }


    /*
    public StreamEventTransformer(List<EventRateTransformationRule> eventRateTransformationRules) {
        this.eventRateTransformationRules = eventRateTransformationRules;
    }
    */

  public void addEventRateTransformationRule(EventRateTransformationRule rule) {
    this.eventRateTransformationRules.add(rule);
  }

  @Override
  public Map<String, Object> transform(Map<String, Object> event) {

    for (EventRateTransformationRule rateRule : eventRateTransformationRules) {
      event = rateRule.transform(event);
    }

    return event;
  }
}
