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
  private static final String[] GREATER_THAN_OR_EQUALS = {"greaterThanOrEquals", ">="};
  private static final String[] LESS_THAN_OR_EQUALS = {"lessThanOrEquals", "<="};

  // This method is for string comparisons (e.g., in the StringInputProcessor)
  public static boolean evaluate(String operator, String inputValue, Object compareValue) {
    double eventNumericValue = 0.0;
    double caseNumericValue = 0.0;

    // Attempt to parse to double for numerical operators, otherwise use string comparison
    try {
      eventNumericValue = Double.parseDouble(inputValue);
      caseNumericValue = Double.parseDouble(compareValue.toString());
    } catch (NumberFormatException e) {
      // If not parseable as number, default to string comparison for "==" and "!="
      if (matches(operator, EQUALS)) {
        return inputValue.equals(compareValue.toString());
      } else if (matches(operator, NOT_EQUALS)) {
        return !inputValue.equals(compareValue.toString());
      } else {
        // For numerical operators, if values are not numbers, it's an invalid comparison.
        // You might want to log this or handle it more gracefully depending on requirements.
        throw new IllegalArgumentException(
            "Cannot perform numerical comparison on non-numeric string values with operator: " + operator);
      }
    }


    if (matches(operator, EQUALS)) {
      return eventNumericValue == caseNumericValue;
    } else if (matches(operator, NOT_EQUALS)) {
      return eventNumericValue != caseNumericValue;
    } else if (matches(operator, GREATER_THAN)) {
      return eventNumericValue > caseNumericValue;
    } else if (matches(operator, LESS_THAN)) {
      return eventNumericValue < caseNumericValue;
    } else if (matches(operator, GREATER_THAN_OR_EQUALS)) {
      return eventNumericValue >= caseNumericValue;
    } else if (matches(operator, LESS_THAN_OR_EQUALS)) {
      return eventNumericValue <= caseNumericValue;
    } else {
      throw new IllegalArgumentException("Unknown operator: " + operator);
    }
  }

  // This method is used by the NumericalInputProcessor.
  public static boolean evaluate(String operator, double inputValue, Object compareValue) {
    double caseNumericValue = Double.parseDouble(compareValue.toString()); // Value from the switch case config

    if (matches(operator, EQUALS)) {
      return inputValue == caseNumericValue;
    } else if (matches(operator, NOT_EQUALS)) {
      return inputValue != caseNumericValue;
    } else if (matches(operator, GREATER_THAN)) {
      // FIX APPLIED: inputValue is already a double.
      return inputValue > caseNumericValue;
    } else if (matches(operator, LESS_THAN)) {
      // FIX APPLIED: inputValue is already a double.
      return inputValue < caseNumericValue;
    } else if (matches(operator, GREATER_THAN_OR_EQUALS)) {
      // FIX APPLIED: inputValue is already a double.
      return inputValue >= caseNumericValue;
    } else if (matches(operator, LESS_THAN_OR_EQUALS)) {
      // FIX APPLIED: inputValue is already a double.
      return inputValue <= caseNumericValue;
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
