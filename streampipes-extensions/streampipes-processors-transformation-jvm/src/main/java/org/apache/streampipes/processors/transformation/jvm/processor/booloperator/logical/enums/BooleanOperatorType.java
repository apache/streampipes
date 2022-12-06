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

package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.enums;

public enum BooleanOperatorType {

  XOR("XOR", "Performs XOR between boolean operands"),
  OR("OR", "Performs OR between boolean operands"),
  AND("AND", "Performs AND between boolean operands"),
  NOT("NOT", "Performs NOT on boolean operand"),
  X_NOR("X-NOR", "Performs X-NOR between boolean operands"),
  NOR("NOR", "Performs NOR between boolean operands");

  private final String operator;
  private final String description;

  BooleanOperatorType(String operator, String description) {
    this.operator = operator;
    this.description = description;
  }

  public String operator() {
    return operator;
  }

  public String description() {
    return description;
  }

  public static BooleanOperatorType getBooleanOperatorType(String operator) {
    if (operator.equals(AND.operator())) {
      return AND;
    } else if (operator.equals(OR.operator())) {
      return OR;
    } else if (operator.equals(XOR.operator())) {
      return XOR;
    } else if (operator.equals(NOT.operator())) {
      return NOT;
    } else if (operator.equals(X_NOR.operator())) {
      return X_NOR;
    } else if (operator.equals(NOR.operator())) {
      return NOR;
    } else {
      throw new UnsupportedOperationException("No match found for " + operator);
    }
  }

}
