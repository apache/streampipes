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

package org.apache.streampipes.sinks.databases.jvm.jdbcclient.utils;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.DbDataTypeFactory;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.DbDescription;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.SupportedDbEngines;
import org.apache.streampipes.vocabulary.XSD;

import java.util.List;

public class SQLStatementUtils {

  /**
   * Checks if the input string is allowed (regEx match and length > 0)
   *
   * @param input           String which is getting matched with the regEx
   * @param regExIdentifier Information about the use of the input. Gets included in the exception message
   * @throws SpRuntimeException If {@code input} does not match with {@link DbDescription#getAllowedRegEx()}
   *                            or if the length of {@code input} is 0
   */
  public static final void checkRegEx(String input, String regExIdentifier, DbDescription dbDescription)
      throws SpRuntimeException {
    if (!input.matches(dbDescription.getAllowedRegEx()) || input.length() == 0) {
      throw new SpRuntimeException(regExIdentifier + " '" + input
          + "' not allowed (allowed: '" + dbDescription.getAllowedRegEx() + "') with a min length of 1");
    }
  }

  /**
   * Creates a SQL-Query with the given Properties (SQL-Injection safe). For nested properties it
   * recursively extracts the information. EventPropertyList are getting converted to a string (so
   * in SQL to a VARCHAR(255)). For each type it uses {@link DbDataTypeFactory#getFromUri(String, SupportedDbEngines)}
   * internally to identify the SQL-type from the runtimeType.
   *
   * @param properties  The list of properties which should be included in the query
   * @param preProperty A string which gets prepended to all property runtimeNames
   * @return A StringBuilder with the query which needs to be executed in order to create the table
   * @throws SpRuntimeException If the runtimeName of any property is not allowed
   */
  public static StringBuilder extractEventProperties(List<EventProperty> properties, String preProperty,
                                                     DbDescription dbDescription)
      throws SpRuntimeException {
    // output: "randomString VARCHAR(255), randomValue INT"
    StringBuilder stringBuilder = new StringBuilder();
    String separator = "";
    for (EventProperty property : properties) {

      // Protection against SqlInjection
      checkRegEx(property.getRuntimeName(), "Column name", dbDescription);

      if (property instanceof EventPropertyNested) {
        // if it is a nested property, recursively extract the needed properties
        StringBuilder tmp = extractEventProperties(((EventPropertyNested) property).getEventProperties(),
            preProperty + property.getRuntimeName() + "_", dbDescription);
        if (tmp.length() > 0) {
          stringBuilder.append(separator).append(tmp);
        }
      } else {
        // Adding the name of the property (e.g. "randomString")
        // Or for properties in a nested structure: input1_randomValue
        // "separator" is there for the ", " part

        if (dbDescription.isColumnNameQuoted()) {
          stringBuilder.append(separator).append("\"").append(preProperty).append(property.getRuntimeName())
              .append("\" ");
        } else {
          stringBuilder.append(separator).append(preProperty).append(property.getRuntimeName()).append(" ");
        }

        // adding the type of the property (e.g. "VARCHAR(255)")
        if (property instanceof EventPropertyPrimitive) {
          stringBuilder.append(DbDataTypeFactory.getFromUri(((EventPropertyPrimitive) property).getRuntimeType(),
              dbDescription.getEngine()));
        } else {
          // Must be an EventPropertyList then
          stringBuilder.append(DbDataTypeFactory.getFromUri(XSD.STRING.toString(), dbDescription.getEngine()));
        }
      }
      separator = ", ";
    }

    return stringBuilder;
  }
}
