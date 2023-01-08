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
import org.apache.streampipes.wrapper.siddhi.query.expression.StreamExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.WithinExpression;

import java.util.List;
import java.util.stream.Collectors;

public abstract class PatternExpression extends Expression {

  private final String pattern;
  private final List<StreamExpression> streamExpressions;
  private WithinExpression withinExpression;

  public PatternExpression(String pattern, List<StreamExpression> streamExpressions) {
    this.pattern = pattern;
    this.streamExpressions = streamExpressions;
  }

  public PatternExpression(String pattern,
                           List<StreamExpression> streamExpressions,
                           WithinExpression withinExpression) {
    this(pattern, streamExpressions);
    this.withinExpression = withinExpression;
  }

  @Override
  public String toSiddhiEpl() {
    String patternExpression = joinWithParenthesis(SiddhiConstants.EMPTY, join(pattern, streamExpressions
            .stream()
            .map(StreamExpression::toSiddhiEpl)
            .collect(Collectors.toList())));

    if (this.withinExpression != null) {
      patternExpression = join(SiddhiConstants.EMPTY, patternExpression, this.withinExpression.toSiddhiEpl());
    }

    return patternExpression;

  }
}
