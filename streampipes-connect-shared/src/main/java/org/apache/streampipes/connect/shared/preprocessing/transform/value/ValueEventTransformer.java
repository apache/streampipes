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

package org.apache.streampipes.connect.shared.preprocessing.transform.value;

import org.apache.streampipes.connect.shared.preprocessing.transform.TransformationRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ValueEventTransformer implements ValueTransformationRule {

  private final List<UnitTransformationRule> unitTransformationRules;
  private List<TimestampTranformationRule> timestampTransformationRules;
  private final List<CorrectionValueTransformationRule> correctionValueTransformationRules;
  private final List<DatatypeTransformationRule> datatypeTransformationRules;

  public ValueEventTransformer(List<ValueTransformationRule> rules) {
    this.unitTransformationRules = new ArrayList<>();
    this.timestampTransformationRules = new ArrayList<>();
    this.correctionValueTransformationRules = new ArrayList<>();
    this.datatypeTransformationRules = new ArrayList<>();

    for (TransformationRule rule : rules) {
      if (rule instanceof UnitTransformationRule) {
        this.unitTransformationRules.add((UnitTransformationRule) rule);
      } else if (rule instanceof TimestampTranformationRule) {
        this.timestampTransformationRules.add((TimestampTranformationRule) rule);
      } else if (rule instanceof CorrectionValueTransformationRule) {
        this.correctionValueTransformationRules.add((CorrectionValueTransformationRule) rule);
      } else if (rule instanceof DatatypeTransformationRule) {
        this.datatypeTransformationRules.add((DatatypeTransformationRule) rule);
      }
    }
  }

  @Override
  public Map<String, Object> transform(Map<String, Object> event) {

    for (UnitTransformationRule rule : unitTransformationRules) {
      event = rule.transform(event);
    }

    for (TimestampTranformationRule rule : timestampTransformationRules) {
      event = rule.transform(event);
    }

    for (var rule : datatypeTransformationRules) {
      event = rule.transform(event);
    }

    for (CorrectionValueTransformationRule rule : correctionValueTransformationRules) {
      event = rule.transform(event);
    }

    return event;
  }

  public List<TimestampTranformationRule> getTimestampTransformationRules() {
    return timestampTransformationRules;
  }

  public void setTimestampTransformationRules(
      List<TimestampTranformationRule> timestampTransformationRules) {
    this.timestampTransformationRules = timestampTransformationRules;
  }
}
