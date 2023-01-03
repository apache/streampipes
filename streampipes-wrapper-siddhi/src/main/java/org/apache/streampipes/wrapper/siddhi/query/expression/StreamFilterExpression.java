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
package org.apache.streampipes.wrapper.siddhi.query.expression;

import org.apache.streampipes.wrapper.siddhi.constants.SiddhiConstants;
import org.apache.streampipes.wrapper.siddhi.query.expression.pattern.PatternCountExpression;

import java.util.List;
import java.util.stream.Collectors;

public class StreamFilterExpression extends StreamExpression {

  private final StreamExpression streamExpression;
  private final List<Expression> filterExpressions;
  private PatternCountExpression patternCountExpression;

  public StreamFilterExpression(StreamExpression streamExpression,
                                List<Expression> filterExpressions) {
    this.streamExpression = streamExpression;
    this.filterExpressions = filterExpressions;
  }

  public StreamFilterExpression(StreamExpression streamExpression,
                                List<Expression> filterExpressions,
                                PatternCountExpression patternCountExpression) {
    this(streamExpression, filterExpressions);
    this.patternCountExpression = patternCountExpression;
  }

  @Override
  public String toSiddhiEpl() {
    String filterExpressions = join(SiddhiConstants.COMMA, this.filterExpressions
        .stream()
        .map(Expression::toSiddhiEpl)
        .collect(Collectors.toList()));

    filterExpressions = join(SiddhiConstants.EMPTY,
        this.streamExpression.toSiddhiEpl(),
        joinWithSquareBracket(SiddhiConstants.EMPTY, filterExpressions));

    if (this.patternCountExpression != null) {
      filterExpressions =
          join(SiddhiConstants.WHITESPACE, filterExpressions, this.patternCountExpression.toSiddhiEpl());
    }

    return filterExpressions;
  }
}
