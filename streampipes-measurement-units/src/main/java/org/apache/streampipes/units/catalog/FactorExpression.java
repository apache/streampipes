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

package org.apache.streampipes.units.catalog;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Evaluates the conversion factors written in the catalog.
 *
 * <p>An expression is a chain of decimal numbers or {@code pi}, joined by {@code *} and
 * {@code /} and evaluated left to right, e.g. {@code 2*pi/60} or
 * {@code 4.4482216152605/0.00064516}. Writing factors this way keeps them exactly as they are
 * defined in the cited sources, and they are rounded to {@code double} only once.</p>
 */
final class FactorExpression {

  private static final MathContext PRECISION = MathContext.DECIMAL128;
  private static final BigDecimal PI = new BigDecimal("3.141592653589793238462643383279502884");

  private FactorExpression() {
  }

  static double evaluate(String expression) {
    String[] tokens = expression.replace(" ", "").split("(?=[*/])|(?<=[*/])");
    if (tokens.length % 2 == 0) {
      throw new IllegalArgumentException("Malformed factor expression: " + expression);
    }

    BigDecimal result = operand(tokens[0], expression);
    for (int i = 1; i < tokens.length; i += 2) {
      BigDecimal operand = operand(tokens[i + 1], expression);
      result = switch (tokens[i]) {
        case "*" -> result.multiply(operand, PRECISION);
        case "/" -> result.divide(operand, PRECISION);
        default -> throw new IllegalArgumentException("Malformed factor expression: " + expression);
      };
    }
    return result.doubleValue();
  }

  private static BigDecimal operand(String token, String expression) {
    if ("pi".equals(token)) {
      return PI;
    }
    try {
      return new BigDecimal(token);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Malformed factor expression: " + expression, e);
    }
  }
}
