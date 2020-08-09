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

package org.apache.streampipes.connect.adapters.mysql;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import org.apache.streampipes.connect.adapter.Adapter;
import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.connect.adapter.exception.ParseException;
import org.apache.streampipes.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.apache.streampipes.connect.adapter.sdk.ParameterExtractor;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.vocabulary.SO;

import java.io.IOException;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.apache.streampipes.sdk.helpers.EpProperties.timestampProperty;

public class MySqlAdapter extends SpecificDataStreamAdapter {

  public static final String ID = "http://streampipes.org/adapter/specific/mysql";

  private static String MYSQL_HOST = "MYSQL_HOST";
  private static String MYSQL_USER = "MYSQL_USER";
  private static String MYSQL_PASS = "MYSQL_PASS";
  private static String MYSQL_DB = "MYSQL_DB";
  private static String MYSQL_TABLE = "MYSQL_TABLE";
  private static String MYSQL_PORT = "MYSQL_PORT";

  private String host;
  private String user;
  private String pass;
  private String database;
  private String table;
  private String port;

  private boolean dataComing = false;
  private List<Column> tableSchema;
  private BinaryLogClient client;

  public MySqlAdapter() {
  }

  public MySqlAdapter(SpecificAdapterStreamDescription adapterDescription) {
    super(adapterDescription);

    getConfigurations(adapterDescription);
  }

  @Override
  public SpecificAdapterStreamDescription declareModel() {
    //TODO: Add Icon
    SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID,
            "MySql Adapter",
            "Creates a data stream for a SQL table")
            .iconUrl("sql.png")
            .requiredTextParameter(Labels.from(MYSQL_HOST, "Hostname", "Hostname of the MySql Server"))
            .requiredTextParameter(Labels.from(MYSQL_USER, "Username", "Username of the user"))
            .requiredTextParameter(Labels.from(MYSQL_PASS, "Password", "Password of the user"))
            .requiredTextParameter(Labels.from(MYSQL_DB, "Database", "Database in which the table is located"))
            .requiredTextParameter(Labels.from(MYSQL_TABLE, "Table", "Table which should be watched"))
            .requiredIntegerParameter(Labels.from(MYSQL_PORT, "Port", "Port of the MySql Server. Default: 3306"), 3306)
            .build();

    description.setAppId(ID);
    return  description;
  }

  @Override
  public void startAdapter() throws AdapterException {
    checkJdbcDriver();
    extractTableInformation();

    // Connect BinaryLogClient
    client = new BinaryLogClient(host, Integer.parseInt(port), user, pass);
    EventDeserializer eventDeserializer = new EventDeserializer();
    eventDeserializer.setCompatibilityMode(
            EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
            EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY
    );
    client.setEventDeserializer(eventDeserializer);
    client.registerEventListener(event -> sendEvent(event));
    try {
      client.connect();
    } catch (IOException e) {
      throw new AdapterException(e.getMessage());
    }
  }

  private void sendEvent(Event event) {
    // An event can contain multiple insertions/updates
    if (event.getHeader().getEventType() == EventType.TABLE_MAP) {
      // Check table and database, if the next event should be streamed
      if (((TableMapEventData) event.getData()).getDatabase().equals(database)
              && ((TableMapEventData) event.getData()).getTable().equals((table))) {
        dataComing = true;
      }
    }
    if (dataComing) {
      if (EventType.isUpdate(event.getHeader().getEventType())) {
        for (Entry<Serializable[], Serializable[]> en : ((UpdateRowsEventData) event.getData()).getRows()) {
          sendChange(en.getValue());
        }
        dataComing = false;
      } else if (EventType.isWrite(event.getHeader().getEventType())) {
        for (Serializable[] s : ((WriteRowsEventData) event.getData()).getRows()) {
          sendChange(s);
        }
        dataComing = false;
      }
    }
  }

  private void sendChange(Serializable[] rows) {
    Map<String, Object> out = new HashMap<>();
    for (int i = 0; i < rows.length; i++) {
      if (rows[i] != null) {
        if (rows[i] instanceof byte[]) {
          // Strings are sent in byte arrays and have to be converted. TODO: Check that encoding is correct
          out.put(tableSchema.get(i).getName(), new String((byte[])rows[i]));
        } else {
          out.put(tableSchema.get(i).getName(), rows[i]);
        }
      } else {
        out.put(tableSchema.get(i).getName(), tableSchema.get(i).getDefault());
      }
    }
    adapterPipeline.process(out);
  }

  @Override
  public void stopAdapter() throws AdapterException {
    try {
      client.disconnect();
    } catch (IOException e) {
      throw new AdapterException("Thrown exception: " + e.getMessage());
    }
  }

  @Override
  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
    return new MySqlAdapter(adapterDescription);
  }

  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription)
          throws AdapterException, ParseException {
    // Load JDBC Driver, connect JDBC Driver, Extract information, disconnect JDBC Driver
    EventSchema eventSchema = new EventSchema();
    GuessSchema guessSchema = new GuessSchema();
    List<EventProperty> allProperties = new ArrayList<>();

    getConfigurations(adapterDescription);

    checkJdbcDriver();
    extractTableInformation();

    for (Column column : tableSchema) {
      if (SO.DateTime.equals(column.getDomainProperty())) {
        allProperties.add(PrimitivePropertyBuilder
                .create(column.getType(), column.getName())
                .label(column.getName())
                .domainProperty(SO.DateTime)
                .build());
      } else {
        allProperties.add(PrimitivePropertyBuilder
                .create(column.getType(), column.getName())
                .label(column.getName())
                .build());
      }

    }

    eventSchema.setEventProperties(allProperties);
    guessSchema.setEventSchema(eventSchema);

    return guessSchema;
  }

  @Override
  public String getId() {
    return ID;
  }

  private void getConfigurations(SpecificAdapterStreamDescription adapterDescription) {
    ParameterExtractor extractor = new ParameterExtractor(adapterDescription.getConfig());

    this.host = extractor.singleValue(MYSQL_HOST, String.class);
    this.user = extractor.singleValue(MYSQL_USER, String.class);
    this.pass = extractor.singleValue(MYSQL_PASS, String.class);
    this.database = extractor.singleValue(MYSQL_DB, String.class);
    this.table = extractor.singleValue(MYSQL_TABLE, String.class);
    this.port = extractor.singleValue(MYSQL_PORT, String.class);
  }

  private void checkJdbcDriver() throws AdapterException {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      throw new AdapterException("MySql Driver not found.");
    }
  }

  private void extractTableInformation() throws AdapterException {
    String server = "jdbc:mysql://" + host + ":" + port + "/" + "?sslMode=DISABLED&allowPublicKeyRetrieval=true";
    ResultSet resultSet = null;
    tableSchema = new ArrayList<>();

    String query = "SELECT COLUMN_NAME, DATA_TYPE, COLUMN_TYPE FROM "
            + "INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ? AND TABLE_SCHEMA = ? ORDER BY "
            + "ORDINAL_POSITION ASC;";

    try (Connection con = DriverManager.getConnection(server, user, pass);
         PreparedStatement statement = con.prepareStatement(query)) {

      statement.setString(1, table);
      statement.setString(2, database);
      resultSet = statement.executeQuery();

      if (resultSet.next()) {
        do {
          String name = resultSet.getString("COLUMN_NAME");
          String dataType = resultSet.getString("DATA_TYPE");
          String columnType = resultSet.getString("COLUMN_TYPE");
          tableSchema.add(new Column(name, dataType, columnType));
        } while(resultSet.next());
      } else {
        // No columns found -> Table/Database does not exist
        throw new IllegalArgumentException("Database/table not found");
      }
    } catch (SQLException e) {
      throw new AdapterException("SqlException: " + e.getMessage()
              + ", Error code: " + e.getErrorCode()
              + ", SqlState: " + e.getSQLState());
    } finally {
      try {
        resultSet.close();
      } catch (Exception e) {}
    }
  }
}
