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

package org.apache.streampipes.sinks.databases.jvm.jdbcclient.model;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.utils.SQLStatementUtils;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.utils.StatementUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class StatementHandler {

  public Statement statement;
  public PreparedStatement preparedStatement;
  /**
   * The parameters in the prepared statement {@code ps} together with their index and data type
   */
  protected HashMap<String, ParameterInformation> eventParameterMap = new HashMap<>();

  public StatementHandler(Statement statement, PreparedStatement preparedStatement) {
    this.statement = statement;
    this.preparedStatement = preparedStatement;
  }

  /**
   * Initializes the variables {@link StatementHandler#eventParameterMap} and {@link StatementHandler#preparedStatement}
   * according to the parameter event.
   *
   * @param event The event which is getting analyzed
   * @throws SpRuntimeException When the tablename is not allowed
   * @throws SQLException       When the prepareStatement cannot be evaluated
   */
  public void generatePreparedStatement(DbDescription dbDescription, TableDescription tableDescription,
                                        Connection connection, final Map<String, Object> event)
      throws SQLException, SpRuntimeException {
    // input: event
    // wanted: INSERT INTO test4321 ( randomString, randomValue ) VALUES ( ?,? );
    eventParameterMap.clear();
    StringBuilder statement1 = new StringBuilder("INSERT INTO ");
    StringBuilder statement2 = new StringBuilder("VALUES ( ");
    SQLStatementUtils.checkRegEx(tableDescription.getName(), "Tablename", dbDescription);
    statement1.append(tableDescription.getName()).append(" ( ");

    // Starts index at 1, since the parameterIndex in the PreparedStatement starts at 1 as well
    extendPreparedStatement(dbDescription, event, statement1, statement2, 1, "", "");

    statement1.append(" ) ");
    statement2.append(" );");
    String finalStatement = statement1.append(statement2).toString();
    this.preparedStatement = connection.prepareStatement(finalStatement);
  }

  /**
   * @param event
   * @param s1
   * @param s2
   * @param index
   * @param preProperty
   * @param prefix
   * @return
   */
  public int extendPreparedStatement(DbDescription dbDescription,
                                     final Map<String, Object> event,
                                     StringBuilder s1,
                                     StringBuilder s2,
                                     int index,
                                     String preProperty,
                                     String prefix)
      throws SpRuntimeException {

    for (Map.Entry<String, Object> pair : event.entrySet()) {
      if (pair.getValue() instanceof Map) {
        index = extendPreparedStatement(dbDescription, (Map<String, Object>) pair.getValue(), s1, s2, index,
            pair.getKey() + "_", prefix);
      } else {
        SQLStatementUtils.checkRegEx(pair.getKey(), "Columnname", dbDescription);
        eventParameterMap.put(pair.getKey(), new ParameterInformation(index,
            DbDataTypeFactory.getFromObject(pair.getValue(), dbDescription.getEngine())));
        if (dbDescription.isColumnNameQuoted()) {
          s1.append(prefix).append("\"").append(preProperty).append(pair.getKey()).append("\"");
        } else {
          s1.append(prefix).append(preProperty).append(pair.getKey());
        }
        s2.append(prefix).append("?");
        index++;
      }
      prefix = ", ";
    }
    return index;
  }

  /**
   * Fills a prepared statement with the actual values base on {@link StatementHandler#eventParameterMap}. If
   * {@link StatementHandler#eventParameterMap} is empty or not complete (which should only happen once in the
   * beginning), it calls
   * {@link StatementHandler#generatePreparedStatement
   * (DbDescription, TableDescription, Connection, Map)} to generate a new one.
   *
   * @param event
   * @param pre
   * @throws SQLException
   * @throws SpRuntimeException
   */
  private void fillPreparedStatement(DbDescription dbDescription, TableDescription tableDescription,
                                     Connection connection, final Map<String, Object> event, String pre)
      throws SQLException, SpRuntimeException {

    //TODO: Possible error: when the event does not contain all objects of the parameter list
    for (Map.Entry<String, Object> pair : event.entrySet()) {
      String newKey = pre + pair.getKey();
      if (pair.getValue() instanceof Map) {
        // recursively extracts nested values
        fillPreparedStatement(dbDescription, tableDescription, connection, (Map<String, Object>) pair.getValue(),
            newKey + "_");
      } else {
        if (!eventParameterMap.containsKey(newKey)) {
          //TODO: start the for loop all over again
          generatePreparedStatement(dbDescription, tableDescription, connection, event);
        }
        ParameterInformation p = eventParameterMap.get(newKey);
        StatementUtils.setValue(p, pair.getValue(), this.getPreparedStatement());
      }
    }
  }

  /**
   * Clears, fills and executes the saved prepared statement {@code ps} with the data found in
   * event. To fill in the values it calls
   * {@link StatementHandler#fillPreparedStatement(DbDescription, TableDescription, Connection, Map, String)}.
   *
   * @param event Data to be saved in the SQL table
   * @throws SQLException       When the statement cannot be executed
   * @throws SpRuntimeException When the table name is not allowed or it is thrown
   *                            by {@link org.apache.streampipes.sinks.databases.jvm.jdbcclient.utils.StatementUtils
   *                            #setValue(ParameterInformation, Object, PreparedStatement)}
   */
  public void executePreparedStatement(DbDescription dbDescription, TableDescription tableDescription,
                                       Connection connection, final Map<String, Object> event)
      throws SQLException, SpRuntimeException {
    if (this.getPreparedStatement() != null) {
      this.preparedStatement.clearParameters();
    }
    fillPreparedStatement(dbDescription, tableDescription, connection, event, "");
    this.preparedStatement.executeUpdate();
  }

  public PreparedStatement getPreparedStatement() {
    return preparedStatement;
  }

  public Statement getStatement() {
    return statement;
  }

  public void setStatement(Statement statement) {
    this.statement = statement;
  }

  public Map getEventParameterMap() {
    return this.eventParameterMap;
  }

  public void putEventParameterMap(String parameterName, ParameterInformation parameterInformation) {
    this.eventParameterMap.put(parameterName, parameterInformation);
  }
}
