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

import org.apache.streampipes.connect.shared.preprocessing.SupportsNestedTransformationRule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class CorrectionValueTransformationRule extends SupportsNestedTransformationRule {

  private static Logger logger = LoggerFactory.getLogger(CorrectionValueTransformationRule.class);

  private final List<String> eventKey;
  private final double correctionValue;
  private final String operator;

  public CorrectionValueTransformationRule(List<String> keys,
                                           double correctionValue, String operator) {
    this.correctionValue = correctionValue;
    this.operator = operator;
    this.eventKey = keys;
  }

  @Override
  protected List<String> getEventKeys() {
    return eventKey;
  }

  @Override
  protected void applyTransformation(Map<String, Object> event, List<String> eventKey) {
    try {
      Object obj = event.get(eventKey.get(0));
      double old;
      if (obj instanceof Number) {
        old = ((Number) obj).doubleValue();
      } else {
        throw new RuntimeException(
            String.format("Selected property `%s` does not contain a numeric value: `%s", eventKey.get(0), obj)
        );
      }

      double corrected = switch (operator) {
        case "MULTIPLY" -> old * correctionValue;
        case "ADD" -> old + correctionValue;
        case "SUBTRACT" -> old - correctionValue;
        case "DIVIDE" -> old / correctionValue;
        default -> old;
      };

      event.put(eventKey.get(0), corrected);
    } catch (ClassCastException e) {
      logger.error(e.toString());
    }
  }
}
