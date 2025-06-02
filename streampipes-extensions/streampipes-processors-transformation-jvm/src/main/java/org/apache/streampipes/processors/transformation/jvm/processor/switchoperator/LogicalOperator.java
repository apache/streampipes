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

package org.apache.streampipes.processors.transformation.jvm.processor.switchoperator;

public class LogicalOperator {
  private static final String[] EQUALS = {"equals", "=="};
  private static final String[] NOT_EQUALS = {"notEquals", "!="};
  private static final String[] GREATER_THAN = {"greaterThan", ">"};
  private static final String[] LESS_THAN = {"lessThan", "<"};
  private static final String[] GREATER_THAN_OR_EQUALS = {"greaterThanOrEquals", ">="}; // New
  private static final String[] LESS_THAN_OR_EQUALS = {"lessThanOrEquals", "<="};     // New
  public static boolean evaluate(String operator, String inputValue, Object compareValue) {
    if (matches(operator, EQUALS)) {
      return inputValue.equals(compareValue.toString());
    } else if (matches(operator, NOT_EQUALS)) {
      return !inputValue.equals(compareValue.toString());
    } else if (matches(operator, GREATER_THAN)) {
      return Double.parseDouble(inputValue) > Double.parseDouble(compareValue.toString());
    } else if (matches(operator, LESS_THAN)) {
      return Double.parseDouble(inputValue) < Double.parseDouble(compareValue.toString());
    } else if (matches(operator, GREATER_THAN_OR_EQUALS)) { // New
      return Double.parseDouble(inputValue) >= Double.parseDouble(compareValue.toString());
    } else if (matches(operator, LESS_THAN_OR_EQUALS)) {   // New
      return Double.parseDouble(inputValue) <= Double.parseDouble(compareValue.toString());
    } else {
      throw new IllegalArgumentException("Unknown operator: " + operator);
    }
  }

  public static boolean evaluate(String operator, double inputValue, Object compareValue) {
    if (matches(operator, EQUALS)) {
      return inputValue == Double.parseDouble(compareValue.toString());
    } else if (matches(operator, NOT_EQUALS)) {
      return inputValue != Double.parseDouble(compareValue.toString());
    } else if (matches(operator, GREATER_THAN)) {
      return inputValue > Double.parseDouble(compareValue.toString());
    } else if (matches(operator, LESS_THAN)) {
      return inputValue < Double.parseDouble(compareValue.toString());
    } else if (matches(operator, GREATER_THAN_OR_EQUALS)) { // New
      return inputValue >= Double.parseDouble(compareValue.toString());
    } else if (matches(operator, LESS_THAN_OR_EQUALS)) {   // New
      return inputValue <= Double.parseDouble(compareValue.toString());
    } else {
      throw new IllegalArgumentException("Unknown operator: " + operator);
    }
  }

  private static boolean matches(String operator, String[] aliases) {
    for (String alias : aliases) {
      if (alias.equals(operator)) {
        return true;
      }
    }
    return false;
  }

}
