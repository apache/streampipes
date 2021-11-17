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

package org.apache.streampipes.sinks.databases.jvm.iotdb;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.JdbcClient;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.DbDataTypeFactory;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.DbDataTypes;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.ParameterInformation;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.SupportedDbEngines;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.utils.SQLStatementUtils;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

import java.sql.SQLException;
import java.sql.Statement;

public class IotDb extends JdbcClient implements EventSink<IotDbParameters> {

  private IotDbParameters params;
  private static Logger LOG;

  private String timestampField;

  private final SupportedDbEngines dbEngine = SupportedDbEngines.IOT_DB;

  @Override
  public void onInvocation(IotDbParameters parameters, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {

    this.params = parameters;
    LOG = parameters.getGraph().getLogger(IotDb.class);
    timestampField = parameters.getTimestampField();

    // tablename is the identifier for the storage group in the IoTDB Adapter (e.g. root.data.table1) in which all
    // time series are written
    //TODO: Add better regular expression
    initializeJdbc(
            parameters.getGraph().getInputStreams().get(0).getEventSchema(),
            parameters,
            dbEngine,
            LOG);

  }

  @Override
  public void onEvent(Event event) {
    try {
      if (event.getRaw().containsKey("value")) {
        // Renaming value. Very ugly
        event.addField("value_1", event.getFieldBySelector("s0::value").getRawValue());
        event.removeFieldBySelector("s0::value");
      }
      save(event);
    } catch (SpRuntimeException e) {
      LOG.error(e.getMessage());
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    closeAll();
  }

  @Override
  protected void save(final Event event) throws SpRuntimeException {
    checkConnected();
    try {
      Long timestampValue = event.getFieldBySelector(timestampField).getAsPrimitive().getAsLong();
      event.removeFieldBySelector(timestampField);
      Statement statement;
      statement = connection.createStatement();
      StringBuilder sb1 = new StringBuilder();
      StringBuilder sb2 = new StringBuilder();
      //TODO: Check for SQL-Injection
      // Timestamp must be in the beginning of the values
      sb1.append("INSERT INTO ").append(this.params.getDbTable()).append("(timestamp, ");
      sb2.append(" VALUES (").append(timestampValue).append(", ");
      for (String s : event.getRaw().keySet()) {
        sb1.append(s).append(", ");
        if (event.getFieldByRuntimeName(s).getRawValue() instanceof String) {
          sb2.append("\"").append(event.getFieldByRuntimeName(s).getRawValue().toString()).append("\", ");
        } else {
          sb2.append(event.getFieldByRuntimeName(s).getRawValue().toString()).append(", ");
        }
      }
      sb1.setLength(sb1.length() - 2);
      sb2.setLength(sb2.length() - 2);
      sb1.append(") ").append(sb2).append(")");
      statement.execute(sb1.toString());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void ensureDatabaseExists(String url, String databaseName) throws SpRuntimeException {
    SQLStatementUtils.checkRegEx(this.params.getDbTable(), "Storage Group name", this.dbDescription);
    try {
      Statement statement = connection.createStatement();
      statement.execute("SET STORAGE GROUP TO " + this.params.getDbTable());
    } catch (SQLException e) {
      // Storage group already exists
      //TODO: Catch other exceptions
    }
  }

  /**
   * Needs to be reimplemented since the IoTDB JDBC implementation does not support the methods used in the
   * JDBC-Client class
   *
   * @param url The JDBC url containing the needed information (e.g. "jdbc:iotdb://127.0.0.1:6667/")
   * @throws SpRuntimeException
   */
  @Override
  protected void ensureTableExists(String url, String databaseName) throws SpRuntimeException {
    int index = 1;
    this.statementHandler.putEventParameterMap("timestamp", new ParameterInformation(index++, DbDataTypeFactory.getLong(dbEngine)));
    for (EventProperty eventProperty : this.tableDescription.getEventSchema().getEventProperties()) {
      try {
        if (eventProperty.getRuntimeName().equals(timestampField.substring(4))) {
          continue;
        }
        Statement statement = null;
        statement = connection.createStatement();
        // The identifier cannot be called "value"
        //TODO: Do not simply add a _1 but look instead, if the name is already taken
        String runtimeName = eventProperty.getRuntimeName();
        if (eventProperty.getRuntimeName().equals("value")) {
          runtimeName = "value_1";
        }
        DbDataTypes datatype = extractAndAddEventPropertyRuntimeType(eventProperty, index++);

        statement.execute("CREATE TIMESERIES "
                + params.getDbTable()
                + "."
                + runtimeName
                + " WITH DATATYPE="
                + datatype.toString()
                + ", ENCODING=PLAIN");
      } catch (SQLException e) {
        // Probably because it already exists
        //TODO: Add better exception handling
        e.printStackTrace();
      }
    }
    //tableExists = true;
  }

  private DbDataTypes extractAndAddEventPropertyRuntimeType(EventProperty eventProperty, int index) {
    // Supported datatypes can be found here: https://iotdb.apache.org/#/Documents/0.8.0/chap2/sec2
    DbDataTypes dataType = DbDataTypes.TEXT;
    if (eventProperty instanceof EventPropertyPrimitive) {
       dataType = DbDataTypeFactory.getFromUri(((EventPropertyPrimitive)eventProperty).getRuntimeType(), SupportedDbEngines.IOT_DB);
      this.statementHandler.putEventParameterMap(eventProperty.getRuntimeName(), new ParameterInformation(index, dataType));
    }

    return dataType;
  }
}
