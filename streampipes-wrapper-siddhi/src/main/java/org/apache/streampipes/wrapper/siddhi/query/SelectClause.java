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
package org.apache.streampipes.wrapper.siddhi.query;

import org.apache.streampipes.wrapper.siddhi.constants.SiddhiConstants;
import org.apache.streampipes.wrapper.siddhi.query.expression.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SelectClause extends SiddhiStatement {

  private final boolean wildcard;
  private final List<Expression> outputProperties;

  private SelectClause(boolean wildcard) {
    this.wildcard = wildcard;
    this.outputProperties = new ArrayList<>();
  }

  private SelectClause(List<Expression> outputProperties) {
    this.wildcard = false;
    this.outputProperties = outputProperties;
  }

  public static SelectClause create() {
    return new SelectClause(false);
  }

  public static SelectClause createWildcard() {
    return new SelectClause(true);
  }

  public static SelectClause create(List<Expression> outputProperties) {
    return new SelectClause(outputProperties);
  }

  public static SelectClause create(Expression... outputProperties) {
    return new SelectClause(Arrays.asList(outputProperties));
  }

  public void addProperty(Expression property) {
    this.outputProperties.add(property);
  }

  private String toWildcardStatement() {
    return join(SiddhiConstants.WHITESPACE, SiddhiConstants.SELECT, SiddhiConstants.ASTERISK);
  }

  private String toPropertyExpressionStatement() {
    String properties = join(SiddhiConstants.COMMA,
            this.outputProperties.stream().map(Expression::toSiddhiEpl).collect(Collectors.toList()));

    return join(SiddhiConstants.WHITESPACE, SiddhiConstants.SELECT, properties);
  }

  @Override
  public String toSiddhiEpl() {
    return wildcard ? toWildcardStatement() : toPropertyExpressionStatement();
  }
}
