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
          DbDataTypes dataType = DbDataTypes.valueOf(resultSet.getString("DATA_TYPE").toUpperCase());
          this.dataTypesHashMap.put(columnName, dataType);
        } while (resultSet.next());
      } else {
        throw new SpRuntimeException("Database or Table does nit exist.");
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
    for (EventProperty property : this.eventSchema.getEventProperties()) {
      if (this.getDataTypesHashMap().get(property.getRuntimeName()) != null) {
        if (property instanceof EventPropertyPrimitive) {
          DbDataTypes dataType = this.getDataTypesHashMap().get(property.getRuntimeName());
          if (!((EventPropertyPrimitive) property).getRuntimeType()
              .equals(DbDataTypeFactory.getDataType(dataType).toString())) {
            throw new SpRuntimeException("Table '" + this.getName() + "' does not match the EventProperties");
          }
        }
      } else {
        throw new SpRuntimeException("Table '" + this.getName() + "' does not match the EventProperties");
      }
    }
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
