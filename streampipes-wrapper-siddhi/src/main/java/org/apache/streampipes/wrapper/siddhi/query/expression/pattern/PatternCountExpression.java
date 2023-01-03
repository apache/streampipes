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
package org.apache.streampipes.wrapper.siddhi.query.expression.pattern;

import org.apache.streampipes.wrapper.siddhi.constants.SiddhiConstants;
import org.apache.streampipes.wrapper.siddhi.query.expression.Expression;

public class PatternCountExpression extends Expression {

  private final String countString;

  public PatternCountExpression(Integer count, PatternCountOperator operator) {
    String countValue = "";
    if (operator == PatternCountOperator.EXACTLY_N) {
      countValue = String.valueOf(count);
    } else if (operator == PatternCountOperator.MIN_N) {
      countValue = count + SiddhiConstants.COLON;
    } else if (operator == PatternCountOperator.MAX_N) {
      countValue = SiddhiConstants.COLON + count;
    }

    this.countString = SiddhiConstants.ANGLE_BRACKET_OPEN
            + countValue
            + SiddhiConstants.ANGLE_BRACKET_CLOSE;
  }

  public PatternCountExpression(Integer minCount, Integer maxCount) {
    this.countString = SiddhiConstants.ANGLE_BRACKET_OPEN
            + minCount
            + SiddhiConstants.COLON
            + maxCount
            + SiddhiConstants.ANGLE_BRACKET_CLOSE;
  }

  @Override
  public String toSiddhiEpl() {
    return this.countString;
  }
}
