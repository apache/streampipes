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
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.utils.SQLStatementUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TableDescription {

  private final String name;
  private boolean tableExists;
  private HashMap<String, DbDataTypes> dataTypesHashMap;

  /**
   * The list of properties extracted from the graph
   */
  private EventSchema eventSchema;

  public TableDescription(String name, EventSchema eventSchema) {
    this.name = name;
    this.tableExists = false;
    this.eventSchema = eventSchema;
  }

  public void extractTableInformation(PreparedStatement preparedStatement, Connection connection,
                                      String queryString, String[] queryParameter) throws SpRuntimeException {

    ResultSet resultSet = null;
    this.dataTypesHashMap = new HashMap<String, DbDataTypes>();

    try {

      preparedStatement = connection.prepareStatement(queryString);

      for (int i = 1; i <= queryParameter.length; i++) {
        preparedStatement.setString(i, queryParameter[i - 1]);
      }

      resultSet = preparedStatement.executeQuery();

      if (resultSet.next()) {
        do {
          String columnName = resultSet.getString("COLUMN_NAME");
          DbDataTypes dataType = DbDataTypes.fromSqlType(resultSet.getString("DATA_TYPE"));
          this.dataTypesHashMap.put(columnName, dataType);
        } while (resultSet.next());
      } else {
        throw new SpRuntimeException("Database or Table does not exist.");
      }
    } catch (SQLException e) {
      throw new SpRuntimeException("SqlException: " + e.getMessage() + ", Error code: " + e.getErrorCode()
          + ", SqlState: " + e.getSQLState());
    } finally {
      try {
        resultSet.close();
      } catch (SQLException throwables) {
      }
    }
  }


  /**
   * Creates a table with the name {@link JdbcConnectionParameters#getDbTable()} and the
   * properties from {@link TableDescription#getEventSchema()}. Calls
   * {@link SQLStatementUtils#extractEventProperties(List, String, DbDescription)} internally with the
   * {@link TableDescription#getEventSchema()} to extract all possible columns.
   *
   * @throws SpRuntimeException If the {@link JdbcConnectionParameters#getDbTable()}  is not allowed, if
   *                            executeUpdate throws an SQLException or if
   *                            {@link SQLStatementUtils#extractEventProperties(List, String, DbDescription)}
   *                            throws an exception
   */
  public void createTable(String createStatement, StatementHandler statementHandler, DbDescription dbDescription,
                          TableDescription tableDescription) throws SpRuntimeException {

    SQLStatementUtils.checkRegEx(tableDescription.getName(), "Tablename", dbDescription);


    StringBuilder statement = new StringBuilder(createStatement);
    statement.append(this.getName()).append(" ( ");
    statement.append(
            SQLStatementUtils.extractEventProperties(this.getEventSchema().getEventProperties(), "", dbDescription))
        .append(" );");

    try {
      statementHandler.statement.executeUpdate(statement.toString());
    } catch (SQLException e) {
      throw new SpRuntimeException(e.getMessage());
    }
  }

  public void validateTable() throws SpRuntimeException {
    List<String> missingCols = new ArrayList<>();
    List<String> typeMismatches = new ArrayList<>();

    for (EventProperty property : this.eventSchema.getEventProperties()) {
      String columnName = property.getRuntimeName();
      DbDataTypes existingType = this.getDataTypesHashMap().get(columnName);

      if (existingType == null) {
        // Column is missing in the table
        missingCols.add("'" + columnName + "'");
      } else if (property instanceof EventPropertyPrimitive) {
        // Check the data type of existing columns
        String expected = ((EventPropertyPrimitive) property).getRuntimeType();
        String actual = DbDataTypeFactory.getDataType(existingType).toString();
        if (!expected.equals(actual)) {
          // Remember this mismatch and keep checking, so all problems are reported together later
          typeMismatches.add("'" + columnName + "' (the data stream provides " + extractTypeName(expected)
                  + " but the table column is " + existingType + ")");
        }
      }
    }
    reportMissingColumns(missingCols);
    reportTypeMismatches(typeMismatches);
  }

  /**
   * Shortens a runtime type URI such as "http://www.w3.org/2001/XMLSchema#float" to a name that is
   * readable for users. Only the part after the '#' is kept, for example "float".
   *
   * @param runtimeType the runtime type of the event property.
   * @return the type name without the namespace, or the unchanged input if it contains no '#'.
   */
  private String extractTypeName(String runtimeType) {
    return runtimeType.substring(runtimeType.lastIndexOf('#') + 1);
  }

  /**
   * Reports the columns that the data stream expects but that do not exist in the table, so the
   * user knows which columns to add. Does nothing if no column is missing.
   *
   * @param missingCols the names of the missing columns, may be empty.
   * @throws SpRuntimeException if at least one column is missing.
   */
  private void reportMissingColumns(List<String> missingCols) throws SpRuntimeException {
    if (missingCols.isEmpty()) {
      return;
    }
    String noun = missingCols.size() == 1 ? "Column " : "Columns ";
    String verb = missingCols.size() == 1 ? " is" : " are";
    throw new SpRuntimeException(noun + formatColumnEnumeration(missingCols) + verb
            + " missing in table '" + this.getName() + "'");
  }

  /**
   * Reports the columns whose data type in the table does not match the data stream, so the user
   * knows which columns have the wrong type. Does nothing if all types match.
   *
   * @param typeMismatches the descriptions of the mismatching columns, may be empty.
   * @throws SpRuntimeException if at least one column has a different data type.
   */
  private void reportTypeMismatches(List<String> typeMismatches) throws SpRuntimeException {
    if (typeMismatches.isEmpty()) {
      return;
    }
    String noun = typeMismatches.size() == 1 ? "column " : "columns ";
    throw new SpRuntimeException("Type mismatch in table '" + this.getName() + "' for "
            + noun + formatColumnEnumeration(typeMismatches));
  }

  /**
   * Formats a list of column names or column problems as a readable enumeration for an error message
   * to report every problem of the existing table in a single message.
   *
   * @param columns the column names or problem descriptions to enumerate.
   * @return the entries joined into one phrase, using "and" before the last entry.
   */
  private String formatColumnEnumeration(List<String> columns) {
    if (columns.size() == 1) {
      return columns.get(0);
    }
    if (columns.size() == 2) {
      return columns.get(0) + " and " + columns.get(1);
    }
    return String.join(", ", columns.subList(0, columns.size() - 1))
            + ", and " + columns.get(columns.size() - 1);
  }

  public boolean tableExists() {
    return tableExists;
  }

  public HashMap<String, DbDataTypes> getDataTypesHashMap() {
    return dataTypesHashMap;
  }

  public String getName() {
    return name;
  }

  public void setDataTypesHashMap(HashMap<String, DbDataTypes> dataTypesHashMap) {
    this.dataTypesHashMap = dataTypesHashMap;
  }

  public void putDataTypeHashMap(String name, DbDataTypes dataType) {
    this.dataTypesHashMap.put(name, dataType);
  }

  public void setTableExists() {
    this.tableExists = true;
  }

  public void setTableMissing() {
    this.tableExists = false;
  }

  public void setEventSchema(EventSchema eventSchema) {
    this.eventSchema = eventSchema;
  }

  public EventSchema getEventSchema() {
    return this.eventSchema;
  }
}
