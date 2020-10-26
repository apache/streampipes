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

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public abstract class Expression {

  protected String join(String delimiter, String... substrings) {
    return join(delimiter, Arrays.asList(substrings));
  }

  protected String joinWithParenthesis(String delimiter, String... substrings) {
    return joinWithParenthesis(delimiter, Arrays.asList(substrings));
  }

  protected String joinWithParenthesis(String delimiter, List<String> substrings) {
    return join(delimiter,
            SiddhiConstants.PARENTHESIS_OPEN,
            join(delimiter, substrings),
            SiddhiConstants.PARENTHESIS_CLOSE);
  }

  protected String joinWithSquareBracket(String delimiter, String... substrings) {
    return joinWithSquareBracket(delimiter, Arrays.asList(substrings));
  }

  protected String joinWithSquareBracket(String delimiter, List<String> substrings) {
    return join(delimiter,
            SiddhiConstants.SQUARE_BRACKET_OPEN,
            join(delimiter, substrings),
            SiddhiConstants.SQUARE_BRACKED_CLOSE);
  }


  protected String join(String delimiter, List<String> substrings) {
    StringJoiner joiner = new StringJoiner(delimiter);
    substrings.forEach(joiner::add);

    return joiner.toString();
  }

  public abstract String toSiddhiEpl();
}
