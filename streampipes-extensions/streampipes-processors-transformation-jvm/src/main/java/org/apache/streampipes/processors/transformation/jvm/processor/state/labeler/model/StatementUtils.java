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

package org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.model;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class StatementUtils {

  private static final Logger LOG = LoggerFactory.getLogger(StatementUtils.class);

  /**
   * Add a label to the input event according to the provided statements
   *
   * @param inputEvent
   * @param value
   * @param statements
   * @return
   */
  public static Event addLabel(Event inputEvent, String labelName, double value, List<Statement> statements) {
    String label = getLabel(value, statements);
    if (label != null) {
      inputEvent.addField(labelName, label);
    } else {
      LOG.info("No condition of statements was fulfilled, add a default case (*) to the statements");
    }

    return inputEvent;
  }


  public static List<Statement> getStatements(List<Double> numberValues, List<String> labelStrings,
                                              List<String> comparators) throws SpRuntimeException {

    List<Statement> result = new ArrayList<>();

    for (int i = 0; i < numberValues.size(); i++) {
      Statement statement = new Statement();
      statement.setLabel(labelStrings.get(i));
      statement.setOperator(comparators.get(i));
      statement.setValue(numberValues.get(i));

      result.add(statement);
    }

    return result;
  }

  /**
   * This method checks if the user input is correct. When not null is returned
   *
   * @param s
   * @return
   */
  public static Statement getStatement(String s) {
    Statement result = new Statement();

    String[] parts = s.split(";");
    // default case
    if (parts.length == 2) {
      if (parts[0].equals("*")) {
        result.setOperator(parts[0]);
        result.setLabel(parts[1]);
        return result;
      } else {
        return null;
      }
    }

    // all other valid cases
    if (parts.length == 3) {

      if (parts[0].equals(">") || parts[0].equals("<") || parts[0].equals("=")) {
        result.setOperator(parts[0]);
      } else {
        return null;
      }

      if (isNumeric(parts[1].replaceAll("-", ""))) {
        result.setValue(Double.parseDouble(parts[1]));
      } else {
        return null;
      }

      result.setLabel(parts[2]);

      return result;
    } else {
      return null;
    }
  }

  private static String getLabel(double calculatedValue, List<Statement> statements) {
    for (Statement statement : statements) {
      if (condition(statement, calculatedValue)) {
        return statement.getLabel();
      }
    }
    return null;
  }

  private static boolean condition(Statement statement, double calculatedValue) {
    if (">".equals(statement.getOperator())) {
      return calculatedValue > statement.getValue();
    } else if (">=".equals(statement.getOperator())) {
      return calculatedValue >= statement.getValue();
    } else if ("<=".equals(statement.getOperator())) {
      return calculatedValue <= statement.getValue();
    } else if ("<".equals(statement.getOperator())) {
      return calculatedValue < statement.getValue();
    } else if ("==".equals(statement.getOperator())) {
      return calculatedValue == statement.getValue();
    } else {
      return false;
    }
  }

  private static boolean isNumeric(final String str) {

    // null or empty
    if (str == null || str.length() == 0) {
      return false;
    }
    return str.chars().allMatch(Character::isDigit);
  }
}
